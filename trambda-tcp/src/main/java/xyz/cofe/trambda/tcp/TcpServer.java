package xyz.cofe.trambda.tcp;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
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
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.log.api.Logger;
import xyz.cofe.trambda.sec.SecurityFilter;

/**
 * TCP Сервер для предоставления сервиса.
 * <p>
 * <h2>Создание простого клиент/сервера</h2>
 * В первую очередь необходимо 
 * <ul>
 * <li>созданный {@link ServerSocket}</li>
 * <li>сам сервис или функция возвращающая ссылку на сервис</li>
 * </ul>
 * 
 * В качестве примера создадим простой сервис,
 * который будет отображать информацию о запущенных
 * процессах в ОС:
 * 
<pre>
<i>// Содержит описание процесса ОС</i>
public class OsProc implements Serializable {
    private final Integer ppid;
    private final Integer pid;
    private final String name;
    private final String cmdLine;
    
    public OsProc(int ppid,int pid,String name,String cmdLine){
        this.ppid = ppid;
        this.pid = pid;
        this.name = name;
        this.cmdLine = cmdLine;
    }

    public Optional&lt;Integer&gt; getPpid(){ 
      return ppid!=null ? Optional.of(ppid) : Optional.empty(); 
    }
    
    public int getPid(){ return pid; }
    public String getName(){ return name; }
    public Optional&lt;String&gt; getCmdline(){ 
      return cmdLine!=null ? Optional.of(cmdLine) : Optional.empty(); 
    }
    
        public static OsProc linuxProc(File file){
        if( file.isDir() ){
            try{
                var statusFile = file.resolve("status");
                
                var status = statusFile.isFile() ? 
                  statusFile.readText(Charset.defaultCharset()) : "";
                  
                var cmdLineFile = file.resolve("cmdline");
                var cmdLine = cmdLineFile.isFile() ? 
                  cmdLineFile.readText(Charset.defaultCharset()) : "";

                var keyVals = Text.splitNewLinesIterable(status)
                    .map(line -&gt; line.split("\\s*:\\s*",2))
                    .filter(kv -&gt; kv.length==2)
                    .map(kv -&gt; Tuple2.of(kv[0].toLowerCase(), kv[1]));

                String name = keyVals.filter(
                  kv-&gt;kv.a().equals("name"))
                  .map(Tuple2::b).first().orElse("?");
                  
                String pid = keyVals.filter(
                  kv-&gt;kv.a().equals("pid"))
                  .map(Tuple2::b).first().orElse("-1");
                  
                String ppid = keyVals.filter(
                  kv-&gt;kv.a().equals("ppid"))
                  .map(Tuple2::b).first().orElse("-1");

                return new OsProc(
                  Integer.parseInt(ppid), 
                  Integer.parseInt(pid),
                  name,
                  cmdLine.replace((char)0,' ')
                );
            } catch( Throwable err ){
                System.err.println("err for "+file+": "+err.getMessage());
            }
        }
        return new OsProc(-1,"?");
    }
}

<i>// Это будет интерфейсом нашого сервиса, 
// через который будет происходить общение</i>
public interface IEnv {
  <i>// Возвращает список процессов</i>
  public List&lt;OsProc&gt; processes();
}

<i>// Это реализация нашего сервиса для Linux</i>
public class LinuxEnv implements IEnv {
    &#64;Override
    public List&lt;OsProc&gt; processes(){
        ArrayList&lt;OsProc&gt; procs = new ArrayList&lt;&gt;();
        File procDir = new File("/proc");
        procDir.dirList().stream()
            .filter( d -&gt; d.getName().matches("\\d+") &amp;&amp; d.isDir() )
            .map(OsProc::linuxProc)
            .forEach(procs::add);
        return procs;
    }
}
</pre>
 *
 * Далее поднимим Tcp сервер
 * <pre>
import java.io.IOException;
import java.net.ServerSocket;
import xyz.cofe.trambda.tcp.TcpServer;

...
TcpServer&lt;IEnv&gt; createServer(IEnv myService, int port){
    ...
    ServerSocket ssocket = null;
    TcpServer&lt;IEnv&gt; server = null;
    try{
        <i>// Создаем сокет</i>
        ssocket = new ServerSocket(port);
        
        <i>// Указываем SoTimeout что бы</i>
        <i>// сервер не повис в ожидании пакета</i>
        ssocket.setSoTimeout(1000*5);

        <i>// Создаем сам сервер</i>
        server = new TcpServer&lt;IEnv&gt;(
            <i>// Передаем сокет, который уже привязан к порту</i>
            ssocket,
            
            <i>// ссылку на сервис</i>
            session -&gt; myService
        );
        
        <i>// Указывем характеристики Thread</i>
        server.setDaemon(true);
        server.setName("server");
        
        <i>// Добавляем подписчиков на сообытия сервера</i>
        server.addListener(System.out::println);
        
        <i>// Запускаем сервер</i>
        server.start();
        return server;
    } catch( IOException error ) {
        log.error( "can't start server", error );
        return;
    }
}
</pre>
* 
* <p> Сервис готов и поднят, осталось написать клиента:
<pre>
Query&lt;IEnv&gt; query = TcpQuery
    // Указываем интерфейс сервиса
    .create(IEnv.class)
    // Указывам адрес и порт, на котором располагается сервис
    .host("localhost")
    .port(port)
    // Создаем клиента
    .build();

// Указываем какие процессы нас интересуют на сервере
var qRegex = "chrome|java";

// Выполняем запрос к серверу
query.apply( env -&gt;
    // Данный код выполняется на сервере
    env.processes().stream()
        .filter( p -&gt; p.getName().matches("(?is).*("+qRegex+").*") )
        .collect(Collectors.toList())
)
// Получаем результат выполнения в клиенте
// И отображаем его в логе
.stream().map(OsProc::toString).forEach(log::info);
</pre>
* 
* <h2>Дополнительные темы</h2>
* <ul>
* <li>Настройка безопасности, см {@link SecurError}</li>
* <li>Подписка на события сервера, см {@link Subscribe}</li>
* </ul>
 * @param <ENV> Класс сервиса
 */
public class TcpServer<ENV> extends Thread implements AutoCloseable {
    private static final Logger log = Logger.of(TcpServer.class);

    /**
     * Сокет через который осуществляется общение
     */
    protected final ServerSocket socket;

    /**
     * Сессии клиентов
     */
    protected final Set<TcpSession<ENV>> sessions;

    /**
     * Возвращает сессии
     * @return Сессии клиентов
     */
    public Set<TcpSession<ENV>> getSessions(){
        return sessions;
    }

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
    protected final SecurityFilter<String,Tuple2<LambdaDump, LambdaNode>> securityFilter;

    /**
     * Создание сервера
     * @param socket сокет
     * @param envBuilder Функция получения сервиса для новой сессии
     * @param securityFilter Функция фильтрации байт-кода
     */
    public TcpServer(ServerSocket socket, Function<TcpSession<ENV>,ENV> envBuilder, SecurityFilter<String, Tuple2<LambdaDump, LambdaNode>> securityFilter){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        if( envBuilder==null )throw new IllegalArgumentException( "envBuilder==null" );
        this.envBuilder = envBuilder;
        this.socket = socket;
        sessions = new ConcurrentSkipListSet<>();
        if( securityFilter !=null ){
            this.securityFilter = securityFilter;
        }else{
            this.securityFilter = x -> List.of();
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
                log.trace("SocketTimeoutException");
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
        TcpSession<ENV> ses = new TcpSession<>(this, sock,envBuilder, securityFilter);
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

    //region session / server shutdown
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
    //endregion
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

    /**
     * Список именнованых издателей серверных событий
     */
    protected final Map<String,Publisher<?>> publishers = new ConcurrentHashMap<>();
    
    /**
     * Создание нового или получение уже существующего издалетя серверных событий
     * @param <T> Тип события
     * @param name Имя издателя
     * @return Издатель
     */
    public <T extends Serializable> Publisher<T> publisher(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        var pub = publishers.computeIfAbsent( name, n -> (Publisher<?>) createPublisher(n) );
        //noinspection unchecked
        return (Publisher<T>) pub;
    }
    
    protected Publisher<?> createPublisher(String name){
        log.info("create publisher {}",name);
        return new Publisher<>();
    }

    protected final Map<Class<?>,Object> proxyPublishers = new ConcurrentHashMap<>();
    
    /**
     * Создание нового или получение уже существующего издалетя класса серверных событий.
     * <p>
     * Издатель заданный классом - это интерфейс, например такой
     * <pre>
     * public interface Events {
     *   Publisher&lt;ServerDemoEvent&gt; defaultPublisher();
     *   Publisher&lt;ServerDemoEvent2&gt; timedEvents();
     * }
     * </pre>
     * Для каждого метода (в данном примере для defaultPublisher и timedEvents)
     * будет зарегистриован ({@link #publisher(java.lang.String) }) свой издатель,
     * имя издателя будет совпадать с именем метода.
     * <p> Грубо будет так:
     * <pre>
     * proxy = new {@link PubProxy}(){
     *   &#64;Override
     *   protected Publisher&lt;?&gt; publisher(Method method){
     *     String publisherName = method.getName();
     *     return TcpServer.this.publisher(publisherName);
     *   }
     * };
     * </pre>
     * @param <T> Класс серверных событий - интерфейс c методами без параметра
     * @param cls Класс серверных событий
     * @return Издатель
     */
    public <T> T publishers(Class<T> cls){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        //noinspection unchecked
        return (T)proxyPublishers.computeIfAbsent(cls, this::createProxyPublisher);
    }

    /**
     * Создание прокси для класса событий
     * @param <T> Класс серверных событий - интерфейс c методами без параметра
     * @param cls Класс серверных событий 
     * @return Издатель
     */
    protected <T> T createProxyPublisher(Class<T> cls){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        var pub = new PubProxy(){
            @Override
            protected Publisher<?> publisher(Method method){
                String publisherName = method.getName();
                return TcpServer.this.publisher(publisherName);
            }
        };
        return pub.proxy(cls);
    }
}
