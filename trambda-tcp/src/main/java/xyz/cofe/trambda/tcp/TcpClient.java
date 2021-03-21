package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.ListenersHelper;

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
    public synchronized void close() throws Exception{
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
    //region processing
    protected void process(Message msg, TcpHeader header){
        if(msg instanceof Pong ){
            while( true ){
                var cons = pongConsumers.poll();
                if( cons==null )break;
                cons.accept((Pong) msg);
            }
        }
    }
    private final Queue<Consumer<Pong>> pongConsumers = new ConcurrentLinkedQueue<>();
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
}
