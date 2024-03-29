package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.fn.Tuple3;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.log.api.Logger;
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.SecurityFilter;

/**
 * Сессия клиента
 * @param <ENV> Класс сервиса предоставляемого клиенту
 */
@SuppressWarnings("unused")
public class TcpSession<ENV> extends Thread implements Comparable<TcpSession<ENV>>, TrEventPublisher {
    private static final Logger log = Logger.of(TcpSession.class);

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
     * Функция фильтра безопасности
     */
    protected final SecurityFilter<String,Tuple2<LambdaDump, LambdaNode>> securityFilter;

    /**
     * Сервер
     */
    protected final TcpServer<ENV> server;

    /**
     * Передача событий от сессии к подписчикам сервера
     */
    protected final Consumer<TrEvent> popupEvent;

    /**
     * Конструктор
     * @param socket сокет
     * @param envBuilder функция получения сервиса
     * @param popupEvent функция передачи события сессии в сервер
     */
    public TcpSession(TcpServer<ENV> server, Socket socket,
                      Function<TcpSession<ENV>,ENV> envBuilder,
                      Consumer<TrEvent> popupEvent){
        this(server, socket,envBuilder,null, popupEvent);
    }

    /**
     * Конструктор
     * @param socket сокет
     * @param envBuilder функция получения сервиса
     * @param securityFilter функция фильтра безопасности
     */
    public TcpSession(TcpServer<ENV> server, Socket socket,
                      Function<TcpSession<ENV>,ENV> envBuilder,
                      SecurityFilter<String, Tuple2<LambdaDump, LambdaNode>> securityFilter,
                      Consumer<TrEvent> popupEvent
    ){
        if( server==null )throw new IllegalArgumentException( "server==null" );
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        if( envBuilder==null )throw new IllegalArgumentException( "envBuilder==null" );
        this.server = server;
        this.securityFilter = Objects.requireNonNullElseGet(securityFilter, () -> x -> List.of());
        this.socket = socket;
        this.proto = new TcpProtocol(socket, sentRawData -> {
            if (sentRawData!=null && sentRawData.message!=null){
                fireEvent(new OutgoingMessageEvent<>(
                    TcpSession.this,
                    sentRawData.message,
                    sentRawData.messageId,
                    sentRawData.headerValues
                ));
            }
        });
        this.popupEvent = popupEvent;
        this.service = envBuilder.apply(this);
    }

    //region server : TcpServer<ENV>
    /**
     * Возвращает сервер
     * @return сервер
     */
    public TcpServer<ENV> getServer(){
        return server;
    }
    //endregion

    //region env : ENV - Предоставляемый сервис клиенту
    /**
     * Предоставляемый сервис клиенту
     */
    protected final ENV service;

    /**
     * Возвращает предоставляемый сервис клиенту
     * @return Предоставляемый сервис клиенту
     */
    public ENV getService(){ return service; }
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
        if( popupEvent!=null )popupEvent.accept(event);
    }

    /**
     * Добавляет событие в очередь
     * @param ev событие
     */
    protected void addEvent(TrEvent ev){
        listeners.addEvent(ev);
        if( popupEvent!=null )popupEvent.accept(ev);
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
        unsubscribeAll();
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
//            sessionContext.set(new WeakReference<>(this));
            Optional<BuffInputStream> prevStrm = Optional.empty();

            try{
                var rpack = proto.receiveRaw(prevStrm);
                if( rpack.isEmpty() ) break;

                prevStrm = rpack.get().b();

                received(rpack.get().a().toReadonly());
            } catch( SocketTimeoutException e ) {
                log.trace("io err, session={} {}", sid, e);
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
        unsubscribeAll();
    }
    //endregion

    //region SessionFinished - Событие завершения сессии
    /**
     * Событие завершения сессии
     */
    public static class SessionFinished implements TrEvent {
        /**
         * Конструктор
         * @param session сессия
         */
        public SessionFinished(TcpSession<?> session){
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.session = session;
        }

        private final TcpSession<?> session;

        /**
         * Возвращает ссылку на сессию
         * @return сессия
         */
        public TcpSession<?> getSession(){ return session; }
    }
    //endregion

    /**
     * Сообщение
     * @param <T> тип сообщения
     * @param <ENV> тип сервиса
     */
    public abstract static class MessageEvent<T extends Message, ENV> implements TrEvent {
        protected final TcpSession<ENV> session;
        protected final T message;

        /**
         * Конструктор
         * @param session сессия
         * @param message сообщение
         */
        public MessageEvent(TcpSession<ENV> session, T message) {
            if( message==null )throw new IllegalArgumentException( "message==null" );
            if( session==null )throw new IllegalArgumentException( "session==null" );
            this.session = session;
            this.message = message;
        }
    }

    /**
     * Входящие сообщение
     * @param <T> тип сообщения
     * @param <ENV> тип сервиса
     */
    public static class IncomingMessageEvent<T extends Message, ENV> extends MessageEvent<T,ENV> {
        protected final TcpHeader header;
        public IncomingMessageEvent(TcpSession<ENV> session, T message, TcpHeader header) {
            super(session, message);
            if( header==null )throw new IllegalArgumentException( "header==null" );
            this.header = header;
        }
    }

    /**
     * Исходящие сообщение
     * @param <T> тип сообщения
     * @param <ENV> тип сервиса
     */
    public static class OutgoingMessageEvent<T extends Message, ENV> extends MessageEvent<T,ENV> {
        protected final T message;
        protected final int messageId;
        protected final HeaderValue<?>[] headerValues;
        public OutgoingMessageEvent(TcpSession<ENV> session, T message, int msgId, HeaderValue<?> ... headerValues) {
            super(session, message);
            this.message = message;
            this.messageId = msgId;
            this.headerValues = headerValues;
        }
    }

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
        fireEvent(new IncomingMessageEvent<Message,ENV>(
            this, msg, header
        ));
        if(msg instanceof Ping ){
            process((Ping) msg, header);
        }else if(msg instanceof Compile){
            process((Compile) msg, header);
        }else if(msg instanceof Execute){
            process((Execute) msg, header);
        }else if(msg instanceof Subscribe){
            process((Subscribe) msg, header);
        }else if(msg instanceof UnSubscribe){
            process((UnSubscribe) msg, header);
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

    protected Method compile(LambdaDump dump, String hash){
        var ln = dump.getLambdaNode();
        var methName = ln!=null ?
            ln.walk()
                .map(LambdaNode::getMethod).filter( Objects::nonNull )
                .map( m -> m.getName()+":"+m.getDescriptor() )
                .reduce( "", (a,b)->a+" "+b )
            : "?";

        log.info("compile '{}' hash {}",
            methName,
            hash
        );

        if( securityFilter !=null ){
            log.debug("inspect byte code for SecurAccess");
            var secAcc = SecurAccess.inspect(dump);

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

        //noinspection deprecation
        return dump
            .restore()
            .classLoader( cb -> new ClassLoader() {
                @Override
                protected void finalize() throws Throwable{
                    try {
                        Logger.of(TcpSession.class).info("ClassLoader finalize()");
                    } finally {
                        super.finalize();
                    }
                }

                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException{
                    if( name!=null && name.equals(cb.javaName().getName()) ){
                        var bytes = cb.toByteCode();
                        Logger.of(TcpSession.class).info("ClassLoader defineClass {}",name);
                        return defineClass(name,bytes,0,bytes.length);
                    }
                    return super.findClass(name);
                }
            })
            .method();
    }

    protected void process(Compile compile,TcpHeader header){
        var sid = header.getSid();
        log.info("compile request, sid={}",sid.map(Objects::toString).orElse("?"));

        try{
            var mdef = compile.getDump();
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
            largs.add(service);

            Object[] args = largs.toArray();

            long t0 = System.currentTimeMillis();
            long t0n = System.nanoTime();

            log.info("m.invoke args {}", Arrays.toString(args));
            Object value = m.invoke(null,args);
            log.info("m.invoke return {}",value);

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
                    log.debug("send execute result, referrer {}",sid.get());
                    proto.send(execRes, TcpHeader.referrer.create(sid.get()));
                } else {
                    log.debug("send execute result");
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
    //region Subscribe / UnSubscribe processing
    protected final Map<String,
        Tuple3<Publisher.Subscriber<Serializable>, Long, AutoCloseable>
        > subscribers = new HashMap<>();

    /**
     * Удаление всех подписчиков на события сервера
     */
    protected void unsubscribeAll(){
        synchronized( subscribers ){
            subscribers.forEach( (pubName,s)->{
                try{
                    s.c().close();
                } catch( Exception e ) {
                    log.error("unsubscribe error",e);
                }
            });
        }
    }

    /**
     * Обработка запроса подписки на события сервера, см {@link Subscribe}
     * @param msg запрос подписки
     * @param header заголовок запроса
     */
    protected void process(Subscribe msg, TcpHeader header){
        var sid = header.getSid();
        var pubName = msg.getPublisher();

        log.info("subscribe, pubName={}, sid={}", pubName, sid.map(Objects::toString).orElse("?"));

        if( pubName==null ){
            log.error("pubName is null");

            try{
                var errmsg = new ErrMessage().message("pubName is null");
                if( sid.isPresent() ){
                    proto.send( errmsg, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send( errmsg );
                }

                proto.send( new ErrMessage().message("pubName is null") );
            } catch( IOException e ) {
                log.error("fail send response", e);
            }
            return;
        }

        var pub = getServer().publisher(pubName);

        synchronized( subscribers ){
            var res = new SubscribeResult();

            if( subscribers.containsKey(pubName) ){
                var t = subscribers.get(pubName);
                res.setSubscribeTime(t.b());
            } else {
                Publisher.Subscriber<Serializable> subscriber = subscriber(pubName);
                long t = System.currentTimeMillis();
                res.setSubscribeTime(t);

                var cl = pub.listen(subscriber);
                subscribers.put(pubName, Tuple3.of(subscriber, t, cl));
            }

            try{
                if( sid.isPresent() ){
                    proto.send(res, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send(res);
                }
                log.debug("subscribed to {}",pubName);
            } catch( IOException e ){
                log.error("fail send response", e);
            }
        }
    }

    /**
     * Отписка от событий сервера
     * @param msg запрос
     * @param header заголовок запроса
     */
    protected void process(UnSubscribe msg, TcpHeader header){
        var sid = header.getSid();
        var pubName = msg.getPublisher();

        log.info("unsubscribe, pubName={}, sid={}", pubName, sid.map(Objects::toString).orElse("?"));

        if( pubName==null ){
            log.error("pubName is null");

            try{
                var errmsg = new ErrMessage().message("pubName is null");
                if( sid.isPresent() ){
                    proto.send( errmsg, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send( errmsg );
                }

                proto.send( new ErrMessage().message("pubName is null") );
            } catch( IOException e ) {
                log.error("fail send response", e);
            }
            return;
        }

        var pub = getServer().publisher(pubName);

        synchronized( subscribers ){
            var res = new UnSubscribeResult();

            if( subscribers.containsKey(pubName) ){
                var t = subscribers.get(pubName);
                res.setSubscribeTime(t.b());

                try{
                    t.c().close();
                } catch( Exception e ) {
                    log.error("unsubscribe close error",e);
                    pub.removeListener(t.a());
                }

                subscribers.remove(pubName);
            } else {
                res.setSubscribeTime(-1);
            }

            try{
                if( sid.isPresent() ){
                    proto.send(res, TcpHeader.referrer.create(sid.get()));
                } else {
                    proto.send(res);
                }
                log.debug("subscribed to {}",pubName);
            } catch( IOException e ){
                log.error("fail send response", e);
            }
        }
    }

    /**
     * Создание proxy подписчика
     * @param name имя издателя
     * @return proxy подписчика
     */
    protected Publisher.Subscriber<Serializable> subscriber(String name){
        return new Publisher.Subscriber<Serializable>() {
            @Override
            public void notification(Serializable ev){
                log.info("send notification");

                ServerEvent sev = new ServerEvent();
                sev.setEvent(ev);
                sev.setPublisher(name);

                try{
                    proto.send(sev);
                } catch( IOException e ) {
                    log.error("fail send response", e);
                }
            }
        };
    }
    //endregion
    //endregion
}
