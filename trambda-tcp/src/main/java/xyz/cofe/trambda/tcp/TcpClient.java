package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.trambda.bc.MethodDef;

public class TcpClient implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TcpClient.class);
    protected final Socket socket;
    protected final TcpProtocol proto;
    private final Thread socketReaderThread;

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

    //region construct() / close()
    public TcpClient(Socket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;
        this.proto = new TcpProtocol(socket);
        socketReaderThread = new Thread(this::reader);
        socketReaderThread.setDaemon(true);
        socketReaderThread.setName("client "+socket.getRemoteSocketAddress());
        socketReaderThread.start();
    }

    @Override
    public synchronized void close() {
        shutdown();
    }

    public synchronized void shutdown() {
        if( socketReaderThread.getId()==Thread.currentThread().getId() ){
            throw new IllegalStateException("can't close from self");
        }

        if( socketReaderThread.isAlive() ){
            socketReaderThread.interrupt();
            try{
                socketReaderThread.join(1000L * 10L);
            }catch( InterruptedException e ){
                log.error("socketReaderThread not respond");
                if( !socket.isClosed() ){
                    try {
                        log.info("close socket");
                        socket.close();
                    } catch( IOException ex ){
                        log.error("socket not closed",ex);
                    }
                }
                log.warn("terminate socketReaderThread");
                socketReaderThread.stop();
            }
        }

        if( !socket.isClosed() ){
            try {
                log.info("close socket");
                socket.close();
            } catch( IOException ex ){
                log.error("socket not closed",ex);
            }
        }
    }
    //endregion

    //region read cycle
    private void reader(){
        log.info("reader started");
        while( true ){
            try{
                var raw = proto.receiveRaw();
                if( raw.isEmpty() )break;

                var rw = raw.get().toReadonly();
                if( !rw.isPayloadChecksumMatched() ){
                    log.warn("checksum fail");
                }

                Message msg = null;
                try{
                    msg = rw.payloadMessage();
                } catch( IOError err ){
                    log.error("payload de serialization fail",err);
                    continue;
                }

                if( msg==null ){
                    continue;
                }

                process(msg, rw.getHeader());
            } catch( SocketTimeoutException e ) {
                log.trace("socket timeout");
            } catch( IOException e ) {
                log.error("socket",e);
                break;
            }
        }
        log.info("reader closed");
    }
    //endregion

    //region client api
    //region processing messages
    private final Map<Integer,Consumer<? extends Message>> responseConsumers = new ConcurrentHashMap<>();
    private final Map<Integer,Consumer<ErrMessage>> errorConsumers = new ConcurrentHashMap<>();
    @SuppressWarnings("FieldMayBeFinal")
    private volatile BiConsumer<ErrMessage,TcpHeader> unbindedErrorConsumer = (err, hdr) -> {
        log.error("accept unbinded error on request {} message: {}", hdr.getReferrer(), err.getMessage());
    };
    @SuppressWarnings("FieldMayBeFinal")
    private volatile BiConsumer<Message,TcpHeader> unbindedMessageConsumer = (msg, hdr) -> {
        log.error("accept unbinded message on request {} detail: {}", hdr.getReferrer(), msg);
    };

    protected void process(Message msg, TcpHeader header){
        log.info("process {}, message.sid {}", msg.getClass(), header.getSid());
        if( msg instanceof Pong ){
            process((Pong) msg, header);
        }else if( msg instanceof ErrMessage ){
            var refIdOpt = header.getReferrer();
            if( refIdOpt.isPresent() ){
                var refId = refIdOpt.get();
                responseConsumers.remove(refId);

                var cons = errorConsumers.remove(refId);
                if( cons!=null ){
                    cons.accept((ErrMessage) msg);
                    return;
                }
            }

            var cons = unbindedErrorConsumer;
            if( cons!=null ){
                cons.accept((ErrMessage) msg, header);
            }
        }else{
            var refIdOpt = header.getReferrer();
            if( refIdOpt.isPresent() ){
                var refId = refIdOpt.get();
                errorConsumers.remove(refId);

                @SuppressWarnings("rawtypes") Consumer cons = responseConsumers.remove(refId);
                if( cons!=null ){
                    try {
                        //noinspection unchecked
                        cons.accept(msg);
                        return;
                    } catch( Throwable err ){
                        log.error("accept message error",err);
                    }
                }
            }

            var cons = unbindedMessageConsumer;
            if( cons!=null ){
                cons.accept(msg, header);
            }
        }
    }

    private final Queue<Consumer<Pong>> pongConsumers = new ConcurrentLinkedQueue<>();
    protected void process(Pong pong, TcpHeader header){
        while( true ){
            var cons = pongConsumers.poll();
            if( cons==null )break;
            cons.accept(pong);
        }
    }

    public class ResultConsumer<Req extends Message,Res extends Message> {
        private final Req req;

        public ResultConsumer(Req req){
            if( req==null )throw new IllegalArgumentException( "req==null" );
            this.req = req;
        }

        private volatile Consumer<? extends Message> consumer;
        public ResultConsumer<Req,Res> onSuccess(Consumer<Res> response){
            if( response==null )throw new IllegalArgumentException( "response==null" );
            consumer = response;
            return this;
        }

        private volatile Consumer<ErrMessage> errConsumer;
        public ResultConsumer<Req,Res> onFail(Consumer<ErrMessage> response){
            if( response==null )throw new IllegalArgumentException( "response==null" );
            errConsumer = response;
            return this;
        }

        public ResultConsumer<Req,Res> configure( Consumer<Req> reqConf ){
            if( reqConf==null )throw new IllegalArgumentException( "reqConf==null" );
            reqConf.accept(req);
            return this;
        }

        public void send(){
            try{
                proto.send(req, msgId -> {
                    if( consumer!=null )responseConsumers.put(msgId,consumer);
                    if( errConsumer!=null )errorConsumers.put(msgId,errConsumer);
                });
            } catch( IOException e ) {
                throw new IOError(e);
            }
        }

        public Res fetch(){
            AtomicReference<Res> res = new AtomicReference<>(null);
            AtomicReference<ErrMessage> err = new AtomicReference<>(null);
            final Object sync = ResultConsumer.this;

            try{
                synchronized( sync ){
                    proto.send(req, msgId -> {
                        responseConsumers.put(msgId, msg -> {
                            synchronized( sync ){
                                //noinspection unchecked
                                res.set((Res) msg);
                                sync.notifyAll();
                            }
                        });
                        errorConsumers.put(msgId, err0 -> {
                            synchronized( sync ){
                                err.set(err0);
                                sync.notifyAll();
                            }
                        });
                    });
                    try{
                        sync.wait();
                    } catch( InterruptedException e ) {
                        throw new IOError(e);
                    }
                }
            } catch( IOException e ) {
                throw new IOError(e);
            }

            var errMsg = err.get();
            if( errMsg!=null ){
                throw new RuntimeException(errMsg.getMessage());
            }

            return res.get();
        }
    }
    //endregion

    //region compile()
    public ResultConsumer<Compile,CompileResult> compile(MethodDef methodDef){
        if( methodDef==null )throw new IllegalArgumentException( "methodDef==null" );
        Compile cmpl = new Compile();
        cmpl.setMethodDef(methodDef);
        return new ResultConsumer<>(cmpl);
    }
    //endregion
    public ResultConsumer<Execute,ExecuteResult> execute(CompileResult cres){
        if( cres==null )throw new IllegalArgumentException( "cres==null" );

        Execute exec = new Execute();
        exec.setKey(cres.getKey());
        exec.setHash(cres.getHash());

        return new ResultConsumer<>(exec);
    }
    //region ping()
    public void ping(Consumer<Pong> consumer){
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        pongConsumers.add(consumer);
        try{
            int msgId = proto.send(new Ping());
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }
    //endregion
    //endregion
}
