package xyz.cofe.trambda.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.trambda.LambdaDump;

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

        if( !socket.isClosed() ){
            try {
                log.info("close socket");
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            } catch( IOException ex ){
                log.error("socket not closed",ex);
            }
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
    }
    //endregion

    //region read cycle
    private void reader(){
        log.info("reader started");
        while( true ){
            try{
                if( !proto.readNow() ){
                    break;
                }
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
    //region compile()
    public ResultConsumer<Compile,CompileResult> compile(LambdaDump methodDef){
        if( methodDef==null )throw new IllegalArgumentException( "methodDef==null" );
        return proto.compile(methodDef);
    }
    //endregion
    //region execute()
    public ResultConsumer<Execute,ExecuteResult> execute(CompileResult cres){
        if( cres==null )throw new IllegalArgumentException( "cres==null" );
        return proto.execute(cres);
    }
    //endregion
    //region subscribe()
    public ResultConsumer<Subscribe,SubscribeResult> subscribe(Subscribe subscribe,Consumer<ServerEvent> listener){
        if( subscribe==null )throw new IllegalArgumentException( "subscribe==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return proto.subscribe(subscribe).onSuccess( m -> {
            log.debug("proto.listenServerEvent");
            proto.listenServerEvent(listener);
        });
    }
    public ResultConsumer<Subscribe,SubscribeResult> subscribe(String publisher,Consumer<ServerEvent> listener){
        if( publisher==null )throw new IllegalArgumentException( "publisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        Subscribe subscribe = new Subscribe();
        subscribe.setPublisher(publisher);
        return subscribe(subscribe,listener);
    }
    //endregion
    //region ping()
    public void ping(Consumer<Pong> consumer){
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        proto.ping(consumer);
    }
    //endregion
    //endregion
}
