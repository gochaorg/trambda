package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.MethodRestore;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.SecurityFilter;

/**
 * Сессия клиента
 * @param <ENV> Класс сервиса предоставляемого клиенту
 */
@SuppressWarnings("unused")
public class TcpSession<ENV> extends Thread implements Comparable<TcpSession<ENV>> {
    private static final Logger log = LoggerFactory.getLogger(TcpSession.class);

    /**
     * Последовательность для генерации id
     */
    private static final AtomicInteger idSeq = new AtomicInteger();

    /**
     * Идентификатор сессии
    */
    public final int sid = idSeq.incrementAndGet();

    /**
     * Сокет сессии
     */
    protected final Socket socket;

    /**
     * Общие функции для протокола TCP как клиента, так и сервера
     */
    protected final TcpProtocol proto;

    /**
     * Предоставляемый сервис клиенту
     */
    protected final ENV env;

    /**
     * Функция фильтра безопасности
     */
    protected final SecurityFilter<String,MethodDef> securityFilter;

    /**
     * Конструктор
     * @param socket сокет
     * @param envBuilder функция получения сервиса
     */
    public TcpSession(Socket socket, Function<TcpSession<ENV>,ENV> envBuilder){
        this(socket,envBuilder,null);
    }

    /**
     * Конструктор
     * @param socket сокет
     * @param envBuilder функция получения сервиса
     * @param securityFilter функция фильтра безопасности
     */
    public TcpSession(Socket socket, Function<TcpSession<ENV>,ENV> envBuilder, SecurityFilter<String,MethodDef> securityFilter){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        if( envBuilder==null )throw new IllegalArgumentException( "envBuilder==null" );
        if( securityFilter ==null ){
            this.securityFilter = x -> List.of();
        }else{
            this.securityFilter = securityFilter;
        }
        this.socket = socket;
        this.proto = new TcpProtocol(socket);
        this.env = envBuilder.apply(this);
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
    //region socketInfo

    /**
     * Возвращает локальный адрес сессии
     * @return локальный адрес сессии
     */
    public SocketAddress getLocalAddress(){ return socket.getLocalSocketAddress(); }

    /**
     * Возвращает адрес клиента
     * @return адрес клиента
     */
    public SocketAddress getRemoteAddress(){ return socket.getRemoteSocketAddress(); }

    /**
     * Возвращает состояние привязки сокета.
     * Примечание. Закрытие сокета не очищает его состояние привязки, что означает, что этот метод вернет истину для закрытого сокета (см. IsClosed ()), если он был успешно привязан до закрытия.
     * @return состояние привязки сокета
     */
    public boolean isBound(){ return socket.isBound(); }

    /**
     * Возвращает закрытое состояние сокета.
     * @return сокет закрыт ?
     */
    public boolean isClosed(){ return socket.isClosed(); }

    /**
     * Возвращает, закрыта ли половина соединения сокета для чтения.
     * @return чтение закрыто
     */
    public boolean isInputShutdown(){ return socket.isInputShutdown(); }

    /**
     * Возвращает, закрыта ли половина записи сокета соединения.
     * @return закрыта запись
     */
    public boolean isOutputShutdown(){ return socket.isOutputShutdown(); }

    /**
     * Проверяет, включен ли SO_KEEPALIVE.
     *
     * Если для TCP-сокета задана опция keepalive и в течение 2 часов через сокет не производился обмен данными в любом направлении (ПРИМЕЧАНИЕ: фактическое значение зависит от реализации), TCP автоматически отправляет одноранговому узлу зонд keepalive. Этот зонд представляет собой сегмент TCP, на который одноранговый узел должен ответить. Ожидается один из трех ответов: 1. Узел отвечает ожидаемым ACK. Приложение не уведомляется (так как все в порядке). TCP отправит еще один зонд после еще 2 часов бездействия. 2. Одноранговый узел отвечает RST, который сообщает локальному TCP, что узел однорангового узла вышел из строя и перезагрузился. Розетка закрыта. 3. Нет ответа от однорангового узла. Розетка закрыта. Эта опция предназначена для обнаружения сбоя однорангового хоста. Действительно только для сокета TCP: SocketImpl*
     * @return включен ли SO_KEEPALIVE.
     */
    public Optional<Boolean> getKeepAlive(){
        try{
            return Optional.of(socket.getKeepAlive());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }

    /**
     * Проверяет, включен ли TCP_NODELAY.
     *
     * Отключите алгоритм Нэгла для этого соединения. Записанные в сеть данные не буферизуются до подтверждения ранее записанных данных.
     *
     * @return включен ли TCP_NODELAY.
     */
    public Optional<Boolean> getTcpNoDelay(){
        try{
            return Optional.of(socket.getTcpNoDelay());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }

    /**
     * Проверяет, включен ли SO_REUSEADDR.
     *
     * Устанавливает SO_REUSEADDR для сокета. Это используется только для MulticastSockets в java и установлено по умолчанию для MulticastSockets.
     * @return включен ли SO_REUSEADDR.
     */
    public Optional<Boolean> getReuseAddress(){
        try{
            return Optional.of(socket.getReuseAddress());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }

    /**
     * Проверяет, включен ли SO_OOBINLINE.
     *
     * Если установлен параметр OOBINLINE, любые срочные данные TCP, полученные через сокет, будут приниматься через входной поток сокета. Когда опция отключена (что по умолчанию), срочные данные автоматически отбрасываются.
     * @return включен ли SO_OOBINLINE.
     */
    public Optional<Boolean> getOOBInline(){
        try{
            return Optional.of(socket.getOOBInline());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }
    //endregion
    //region equals(), hashCode(), compareTo()
    @Override
    public boolean equals(Object o){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        //noinspection rawtypes
        TcpSession that = (TcpSession) o;
        return sid == that.sid;
    }

    @Override
    public int hashCode(){
        return Objects.hash(sid);
    }

    @Override
    public int compareTo(TcpSession o){
        if( o==null )return -1;
        return Integer.compare(sid, o.sid);
    }
    //endregion
    //region close()
    public void close(){
        if( !socket.isClosed() ){
            try{
                socket.close();
            } catch( IOException e ) {
                log.error("socket info",e);
            }
        }
    }
    //endregion
    //region run()
    @Override
    public void run(){
        while( true ){
            try{
                var rpack = proto.receiveRaw();
                if( rpack.isEmpty() ) break;

                received(rpack.get().toReadonly());
            } catch( SocketTimeoutException e ) {
                log.debug("io err, session={} {}", sid, e);
                System.out.println("try repeat read");
            } catch( SocketException e ){
                if( e.getMessage()!=null && e.getMessage().matches("(?i).*socket\\s+closed.*") ){
                    log.info("socket closed");
                }else{
                    log.error("socket err",e);
                }
                break;
            } catch( IOException e ) {
                log.warn("io err, session={} {}", sid,e);
                break;
            }
        }

        if( !socket.isClosed() ){
            try{
                log.info("close socket {}",socket.getRemoteSocketAddress());
                socket.close();
            } catch( IOException e ) {
                log.warn("socket close error");
            }
        }

        fireEvent(new SessionFinished(this));
    }
    //endregion

    //region processing message
    //region decode message
    protected void received(RawPackReadonly pack){
        if( !pack.isPayloadChecksumMatched() ){
            log.warn("received bad payload");
            return;
        }

        Message msg;
        try{
            msg = pack.payloadMessage();
        } catch( IOError err ){
            log.error("payload de serialization fail",err);
            return;
        }

        if( msg==null ){
            return;
        }

        process(msg, pack.getHeader());
    }
    protected void process(Message msg, TcpHeader header){
        if(msg instanceof Ping ){
            process((Ping) msg, header);
        }else if(msg instanceof Compile){
            process((Compile) msg, header);
        }else if(msg instanceof Execute){
            process((Execute) msg, header);
        }
    }
    //endregion
    //region ping message
    @SuppressWarnings("unused")
    protected void process(Ping ping, TcpHeader header){
        try{
            var sid = header.getSid();
            if( sid.isPresent() ){
                proto.send(new Pong(), TcpHeader.referrer.create(sid.get()));
            }else {
                proto.send(new Pong());
            }
        } catch( IOException e ) {
            log.error("fail send response");
        }
    }
    //endregion
    //region compile message
    protected final AtomicInteger compileId = new AtomicInteger();
    protected final Map<Integer, Method> compiled = new ConcurrentHashMap<>();
    protected final Map<String, Integer> methodDefHash2compileKey = new ConcurrentHashMap<>();

    protected Method compile(MethodDef mdef, String hash){
        log.info("compile '{}' hash {}",mdef.getName(),hash);

        if( securityFilter !=null ){
            log.debug("inspect byte code for SecurAccess");
            var secAcc = SecurAccess.inspect(mdef);

            if( log.isTraceEnabled() ){
                secAcc.forEach(sa -> log.trace("SecurAccess: {}",sa));
            }

            var secMsgs = securityFilter.validate(secAcc);
            if( secMsgs.stream().anyMatch(m -> !m.isAllow()) ){
                log.info("Lambda contains denied byte code");
                secMsgs.forEach(m -> {
                    if( log.isDebugEnabled() ){
                        if( m.isAllow() ){
                            log.info("allow: security message=\"{}\" access=\"{}\"", m.getMessage(), m.getAccess());
                        }
                    }
                    if( !m.isAllow() ){
                        log.info("deny: security message=\"{}\" access=\"{}\"", m.getMessage(), m.getAccess());
                    }
                });
                throw new SecurError(secMsgs);
            }
        }

        var clName = TcpSession.class.getName().toLowerCase()+".Build1";
        var methName = "lambda1";

        log.debug("generate bytecode "+clName+" method "+methName);
        var byteCode = new MethodRestore().className(clName).methodName(methName).methodDef(mdef).generate();

        ClassLoader cl = createClassLoader(clName, byteCode);

        log.debug("try load class "+clName);
        //noinspection rawtypes
        Class c;
        try{
            c = Class.forName(clName,true,cl);
        } catch( ClassNotFoundException e ) {
            throw new Error(e);
        }

        Method m = null;
        log.debug("find method "+methName);
        for( var delMeth : c.getDeclaredMethods() ){
            if( delMeth.getName().equals(methName) ){
                m = delMeth;
                break;
            }
        }

        if( m==null ){
            throw new Error("compiled method '"+methName+"' not found");
        }

        return m;
    }
    protected ClassLoader createClassLoader(String clName, byte[] byteCode){
        log.debug("create classloader");
        return new ClassLoader(ClassLoader.getSystemClassLoader()) {
            @Override
            protected void finalize() throws Throwable{
                try {
                    LoggerFactory.getLogger(TcpSession.class).info("ClassLoader finalize()");
                } finally {
                    super.finalize();
                }
            }

            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException{
                if( name!=null && name.equals(clName) ){
                    return defineClass(name,byteCode,0,byteCode.length);
                }
                return super.findClass(name);
            }
        };
    }
    protected void process(Compile compile,TcpHeader header){
        var sid = header.getSid();
        log.info("compile request, sid={}",sid.map(Objects::toString).orElse("?"));

        try{
            var mdef = compile.getMethodDef();
            if( mdef==null ){
                throw new IllegalArgumentException("compile.getMethodDef() == null");
            }

            var mdefBytes = Serializer.toBytes(mdef);
            var hash = Text.encodeHex(Hash.md5(mdefBytes,0,mdefBytes.length));
            log.debug("mdef hash {}",hash);

            CompileResult cres = new CompileResult();
            int cid;

            if( methodDefHash2compileKey.containsKey(hash) ){
                cid = methodDefHash2compileKey.get(hash);
                var meth = compiled.get(cid);
                if( meth==null ){
                    log.warn("compiled method not found for key={} hash={}",cid, hash);
                }
            }else {
                var m = compile(mdef,hash);
                cid = compileId.incrementAndGet();

                log.info("compiled {} hash {} id {}",m,hash, this.sid);
                compiled.put(cid,m);
            }

            cres.setKey(cid);
            cres.setHash(hash);

            try{
                if( sid.isPresent() ){
                    proto.send(cres, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send(cres);
                }
            } catch( IOException e ) {
                log.error("fail send response", e);
            }
        } catch( Throwable err ){
            log.error("compile fail {}",err.getMessage(),err);
            try{
                var msg = new ErrMessage().error(err);
                if( sid.isPresent() ){
                    proto.send(msg, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send(msg);
                }
            } catch( IOException e ) {
                log.error("fail send response", e);
            }
        }
    }
    //endregion
    //region execute message
    protected void process(Execute exec, TcpHeader header){
        var sid = header.getSid();
        log.info("execute request, sid={}",sid.map(Objects::toString).orElse("?"));

        try {
            if( exec.getKey() == null && exec.getHash() == null ){
                throw new IllegalArgumentException("execute method key and hash is null");
            }

            var m = compiled.get( exec.getKey()!=null ? exec.getKey() : methodDefHash2compileKey.get(exec.getHash()) );
            if( m==null ){
                throw new IllegalArgumentException("execute method not found " + exec);
            }

            List<Object> largs = new ArrayList<>();
            var cargs = exec.getCapturedArgs();
            if( cargs!=null ){
                log.debug("execute args {}",cargs);
                largs.addAll(cargs);
            }
            largs.add(env);

            Object[] args = largs.toArray();

            long t0 = System.currentTimeMillis();
            long t0n = System.nanoTime();

            Object value = m.invoke(null,args);

            long t1 = System.nanoTime();
            long t1n = System.currentTimeMillis();

            ExecuteResult execRes = new ExecuteResult();
            execRes.setValue(value);
            execRes.setStarted(t0);
            execRes.setStartedNano(t0n);
            execRes.setFinished(t1);
            execRes.setFinishedNano(t1n);

            try{
                if( sid.isPresent() ){
                    proto.send(execRes, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send(execRes);
                }
            } catch( IOException e ) {
                log.error("fail send response", e);
            }
        } catch( Throwable err ){
            log.error("execute fail {}",err.getMessage(),err);
            try{
                var msg = new ErrMessage().error(err);
                if( sid.isPresent() ){
                    proto.send(msg, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send(msg);
                }
            } catch( IOException e ) {
                log.error("fail send response", e);
            }
        }
    }
    //endregion

    //endregion

    //region SessionFinished
    public static class SessionFinished implements TrEvent {
        public SessionFinished(TcpSession<?> session){
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.session = session;
        }

        private final TcpSession<?> session;
        public TcpSession<?> getSession(){ return session; }
    }
    //endregion
}
