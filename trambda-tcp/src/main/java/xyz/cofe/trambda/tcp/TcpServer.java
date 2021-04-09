package xyz.cofe.trambda.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.SecurFilter;

/**
 * TCP Сервер для предоставления сервиса
 * @param <ENV> Класс сервиса
 */
public class TcpServer<ENV> extends Thread implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    /**
     * Сокет через который осуществляется общение
     */
    protected final ServerSocket socket;

    /**
     * Сессии клиентов
     */
    protected final Set<TcpSession<ENV>> sessions;

    /**
     * Информация когда было уведомление о закрытии сессии: ses.id / System.currentTimeMillis()
     *
     * <p>
     * Возможно два сценария закрытия сессии
     *
     * <ol>
     *     <li>
     *         Нормальное закрытие сессии, сессия сама извещает о завершении
     *         {@link #sesListener}
     *     </li>
     *     <li>
     *         Аварийное закрытие сессии, сессия не извещает о завершении
     *         {@link #checkTerminatedSessions}
     *     </li>
     * </ol>
     */
    protected final Map<Integer,Long> fireClosed = new ConcurrentHashMap<>();

    /**
     * Функция получения сервиса для новой сессии
     */
    protected final Function<TcpSession<ENV>,ENV> envBuilder;

    /**
     * Функция фильтрации байт-кода
     */
    protected final SecurFilter<String,MethodDef> securFilter;

    /**
     * Создание сервера
     * @param socket сокет
     * @param envBuilder Функция получения сервиса для новой сессии
     * @param securFilter Функция фильтрации байт-кода
     */
    public TcpServer(ServerSocket socket, Function<TcpSession<ENV>,ENV> envBuilder, SecurFilter<String,MethodDef> securFilter ){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        if( envBuilder==null )throw new IllegalArgumentException( "envBuilder==null" );
        this.envBuilder = envBuilder;
        this.socket = socket;
        sessions = new ConcurrentSkipListSet<>();
        if( securFilter!=null ){
            this.securFilter = securFilter;
        }else{
            this.securFilter = x -> List.of();
        }
    }

    /**
     * Создание сервера
     * @param socket сокет
     * @param envBuilder Функция получения сервиса для новой сессии
     */
    public TcpServer(ServerSocket socket, Function<TcpSession<ENV>,ENV> envBuilder ){
        this(socket,envBuilder,null);
    }

    @Override
    public void run(){
        while( true ){
            if( Thread.currentThread().isInterrupted() ){
                log.info("interrupted");
                break;
            }

            if( socket.isClosed() ){
                log.info("socket closed");
                break;
            }

            try{
                sessions.add(create(socket.accept()));
            } catch( SocketTimeoutException e ){
                log.warn("SocketTimeoutException");
                if( Thread.currentThread().isInterrupted() ){
                    log.info("interrupted");
                    break;
                }
                checkTerminatedSessions();
            } catch( SocketException e ){
                if( e.getMessage()!=null && e.getMessage().matches("(?i).*socket\\s+closed.*") ){
                    log.info("socket closed");
                }else{
                    log.error("socket err",e);
                }
                break;
            } catch( IOException e ) {
                log.error("accept", e);
                break;
            }
        }

        closeSocket();
        closeSessions();
    }

    /**
     * Возвращает значение SoTimeout {@link Socket#setSoTimeout(int)} для сессии
     * @return по умолчанию 3000 мс
     */
    protected int sessionSoTimeout(){ return 1000*3; }

    /**
     * Создание сессии
     * @param sock сокет
     * @return сессия
     * @see #sessionSoTimeout()
     * @see #addSesListener(TcpSession)
     * @see SessionCreated
     */
    protected TcpSession<ENV> create(Socket sock){
        TcpSession<ENV> ses = new TcpSession<>(sock,envBuilder,securFilter);
        try{
            sock.setSoTimeout(sessionSoTimeout());
        } catch( SocketException e ) {
            log.warn("can't set so timeout");
        }
        ses.setDaemon(true);
        ses.setName("session#"+ses.sid +"("+sock.getRemoteSocketAddress()+")");

        addSesListener(ses);
        fireEvent(new SessionCreated(this,ses));

        log.info("starting session {}",ses.getName());
        ses.start();
        return ses;
    }

    /**
     * Завершение всех сессий и остановка сервера
     * @see #closeSocket()
     * @see #closeSessions()
     */
    public synchronized void shutdown(){
        log.info("shutdown");
        closeSocket();
        closeSessions();
    }

    /**
     * Закрытие сокета
     */
    protected void closeSocket(){
        if( !socket.isClosed() ){
            try{
                log.info("close socket");
                socket.close();
            } catch( IOException e ) {
                log.info("socket close error",e);
            }
        }
    }

    /**
     * Таймаут согласно которому сессия должна быть завершена
     * @return 5000 мс
     */
    protected long sessionCloseTimeout(){ return 1000L * 5L ; }

    /**
     * Завершение всех сессий
     * @see #sessionCloseTimeout()
     */
    protected void closeSessions(){
        log.info("close sessions");
        for( var ses : sessions ){
            if( ses.isAlive() ){
                ses.close();
                try{
                    ses.join( sessionCloseTimeout() );
                } catch( InterruptedException e ) {
                    log.warn("session {} close not responsed", ses.sid);
                    ses.stop();
                }
            }
        }
    }

    /**
     * Добавление подписчика sesListener на события сессии
     * @param ses сессия
     */
    protected void addSesListener(TcpSession<ENV> ses){
        if( ses==null )throw new IllegalArgumentException( "ses==null" );
        ses.addListener(sesListener);
    }

    /**
     * Подписчик на событие завершения сессии
     * @see SessionClosed
     * @see #fireClosed
     */
    private final TrListener sesListener = ev -> {
        withQueue(()-> {
            if( ev instanceof TcpSession.SessionFinished ){
                var e = (TcpSession.SessionFinished) ev;
                var ses = e.getSession();
                if( ses == null ) return;
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized( ses ){
                    if( !fireClosed.containsKey(ses.sid) ){
                        //noinspection unchecked,rawtypes,rawtypes
                        fireEvent(new SessionClosed(this, ses));
                        fireClosed.put(ses.sid, System.currentTimeMillis());
                    }
                }
            }
        });
        cleanup_fireClosed();
    };

    /**
     * Периодично вызывается в цикле {@link #run()} для извещения завершенных сессий
     * @see #cleanup_fireClosed()
     */
    private void checkTerminatedSessions(){
        var deadSessions = new HashSet<TcpSession<ENV>>();
        withQueue(()->{
            for( var ses : sessions ){
                if( ses==null )return;
                if( ses.isAlive() )continue;
                deadSessions.add(ses);

                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized( ses ){
                    if(!fireClosed.containsKey(ses.sid)){
                        //noinspection unchecked,rawtypes,rawtypes
                        fireEvent(new SessionClosed(this,ses));
                        fireClosed.put(ses.sid,System.currentTimeMillis());
                    }
                }
            }
        });

        if( !deadSessions.isEmpty() ){
            log.info("remove closed sessions, count={}",deadSessions.size());
            deadSessions.forEach( s -> log.debug("remove closed session id={} name={}",s.sid,s.getName()));
        }
        sessions.removeAll(deadSessions);

        cleanup_fireClosed();
    }

    /**
     * Удаляет информацию {@link #fireClosed} о уже завершенных сессия
     */
    private void cleanup_fireClosed(){
        var sesTo = sessionSoTimeout();
        var tmax = sesTo>0 ? sesTo*3 : 1000 * 15;
        var now = System.currentTimeMillis();
        fireClosed.entrySet().stream().filter( e -> (now - e.getValue())>tmax )
            .map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet())
            .forEach(fireClosed::remove);
    }

    /**
     * Завершение работы сервера
     * @throws Exception Ошибки...
     */
    @Override
    public void close() throws Exception {
        shutdown();
    }

    //region listeners
    protected final ListenersHelper<TrListener,TrEvent> listeners = new ListenersHelper<>(TrListener::trEvent);

    /**
     * Проверка наличия подписчика в списке обработки
     * @param listener подписчик
     * @return true - есть в списке обработки
     */
    public boolean hasListener(TrListener listener){
        return listeners.hasListener(listener);
    }

    /**
     * Получение списка подписчиков
     * @return подписчики
     */
    public Set<TrListener> getListeners(){
        return listeners.getListeners();
    }

    /**
     * Добавление подписчика.
     * @param listener Подписчик.
     * @return Интерфес для отсоединения подписчика
     */
    @SuppressWarnings("UnusedReturnValue")
    public AutoCloseable addListener(TrListener listener){
        return listeners.addListener(listener);
    }

    /**
     * Добавление подписчика.
     * @param listener Подписчик.
     * @param weakLink true - добавить как weak ссылку / false - как hard ссылку
     * @return Интерфес для отсоединения подписчика
     */
    public AutoCloseable addListener(TrListener listener, boolean weakLink){
        return listeners.addListener(listener, weakLink);
    }

    /**
     * Удаление подписчика из списка обработки
     * @param listener подписчик
     */
    public void removeListener(TrListener listener){
        listeners.removeListener(listener);
    }

    /**
     * Удаление всех подписчиков
     */
    public void removeAllListeners(){
        listeners.removeAllListeners();
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param run блок кода
     */
    protected void withQueue(Runnable run){
        listeners.withQueue(run);
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param run блок кода
     * @return возвращаемое значение
     */
    protected <T> T withQueue(Supplier<T> run){
        return listeners.withQueue(run);
    }

    /**
     * Рассылка уведомления подписчикам
     * @param event уведомление
     */
    protected void fireEvent(TrEvent event){
        listeners.fireEvent(event);
    }

    /**
     * Добавляет событие в очередь
     * @param ev событие
     */
    protected void addEvent(TrEvent ev){
        listeners.addEvent(ev);
    }

    /**
     * Отправляет события из очереди подписчикам
     */
    protected void runEventQueue(){
        listeners.runEventQueue();
    }
    //endregion

    //region SessionClosed

    /**
     * Событие о завершении сессии
     */
    public static class SessionClosed<ENV> implements TrEvent {
        private final TcpServer<ENV> server;
        private final TcpSession<ENV> session;
        public SessionClosed(TcpServer<ENV> server,TcpSession<ENV> session){
            if( server==null )throw new IllegalArgumentException( "server==null" );
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.server = server;
            this.session = session;
        }

        /**
         * Возвращает сервер
         * @return сервер
         */
        public TcpServer<ENV> getServer(){
            return server;
        }

        /**
         * Возвращает сессию
         * @return сессия
         */
        public TcpSession<ENV> getSession(){
            return session;
        }

        public String toString(){
            return SessionClosed.class.getSimpleName()+" session.id="+session.sid;
        }
    }
    //endregion
    //region SessionCreated
    /**
     * Событие о создании сессии
     */
    public static class SessionCreated<ENV> implements TrEvent {
        private final TcpServer<ENV> server;
        private final TcpSession<ENV> session;
        public SessionCreated(TcpServer<ENV> server,TcpSession<ENV> session){
            if( server==null )throw new IllegalArgumentException( "server==null" );
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.server = server;
            this.session = session;
        }

        /**
         * Возвращает сервер
         * @return сервер
         */
        public TcpServer<ENV> getServer(){
            return server;
        }

        /**
         * Возвращает сессию
         * @return сессия
         */
        public TcpSession<ENV> getSession(){
            return session;
        }

        public String toString(){
            return SessionCreated.class.getSimpleName()+" session.id="+session.sid;
        }
    }
    //endregion
}
