package xyz.cofe.trambda.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.ListenersHelper;

public class TcpServer extends Thread implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    protected final ServerSocket socket;
    protected final Set<TcpSession> sessions;
    protected final Map<Integer,Long> fireClosed = new ConcurrentHashMap<>();

    public TcpServer(ServerSocket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;
        sessions = new ConcurrentSkipListSet<>();
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

    private int sessionSoTimeout(){ return 1000*3; }

    protected TcpSession create(Socket sock){
        TcpSession ses = new TcpSession(sock);
        try{
            sock.setSoTimeout(1000*3);
        } catch( SocketException e ) {
            log.warn("can't set so timeout");
        }
        ses.setDaemon(true);
        ses.setName("session#"+ses.id+"("+sock.getRemoteSocketAddress()+")");

        addSesListener(ses);
        fireEvent(new SessionCreated(this,ses));

        log.info("starting session {}",ses.getName());
        ses.start();
        return ses;
    }

    public synchronized void shutdown(){
        log.info("shutdown");
        closeSocket();
        closeSessions();
    }

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
    protected void closeSessions(){
        log.info("close sessions");
        for( var ses : sessions ){
            if( ses.isAlive() ){
                ses.close();
                try{
                    ses.join(1000L * 5L );
                } catch( InterruptedException e ) {
                    log.warn("session {} close not responsed", ses.id);
                    ses.stop();
                }
            }
        }
    }

    protected void addSesListener(TcpSession ses){
        if( ses==null )throw new IllegalArgumentException( "ses==null" );
        ses.addListener(sesListener);
    }
    private final TrListener sesListener = ev -> {
        withQueue(()-> {
            if( ev instanceof TcpSession.SessionFinished ){
                var e = (TcpSession.SessionFinished) ev;
                var ses = e.getSession();
                if( ses == null ) return;
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized( ses ){
                    if( !fireClosed.containsKey(ses.id) ){
                        fireEvent(new SessionClosed(this, ses));
                        fireClosed.put(ses.id, System.currentTimeMillis());
                    }
                }
            }
        });
        cleanup_fireClosed();
    };

    private void checkTerminatedSessions(){
        withQueue(()->{
            for( var ses : sessions ){
                if( ses==null )return;
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized( ses ){
                    if(!fireClosed.containsKey(ses.id)){
                        fireEvent(new SessionClosed(this,ses));
                        fireClosed.put(ses.id,System.currentTimeMillis());
                    }
                }
            }
        });
        cleanup_fireClosed();
    }
    private void cleanup_fireClosed(){
        var sesTo = sessionSoTimeout();
        var tmax = sesTo>0 ? sesTo*3 : 1000 * 15;
        var now = System.currentTimeMillis();
        fireClosed.entrySet().stream().filter( e -> (now - e.getValue())>tmax )
            .map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet())
            .forEach(fireClosed::remove);
    }

    @Override
    public void close() throws Exception{
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
    public static class SessionClosed implements TrEvent {
        private final TcpServer server;
        private final TcpSession session;
        public SessionClosed(TcpServer server,TcpSession session){
            if( server==null )throw new IllegalArgumentException( "server==null" );
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.server = server;
            this.session = session;
        }

        public TcpServer getServer(){
            return server;
        }
        public TcpSession getSession(){
            return session;
        }

        public String toString(){
            return SessionClosed.class.getSimpleName()+" session.id="+session.getId();
        }
    }
    //endregion
    //region SessionCreated
    public static class SessionCreated implements TrEvent {
        private final TcpServer server;
        private final TcpSession session;
        public SessionCreated(TcpServer server,TcpSession session){
            if( server==null )throw new IllegalArgumentException( "server==null" );
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.server = server;
            this.session = session;
        }

        public TcpServer getServer(){
            return server;
        }
        public TcpSession getSession(){
            return session;
        }

        public String toString(){
            return SessionCreated.class.getSimpleName()+" session.id="+session.getId();
        }
    }
    //endregion
}
