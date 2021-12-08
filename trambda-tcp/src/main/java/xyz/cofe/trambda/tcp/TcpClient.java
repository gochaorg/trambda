package xyz.cofe.trambda.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.log.api.Logger;

/**
 * Клиент для {@link TcpSession}/{@link TcpServer}.
 * Оперирует уже готовым байт-кодом для отправки.
 *
 * <br>
 * Для работы с лямбдами используйте {@link TcpQuery}
 *
 * <br>
 * Содержит в себе фоновый поток ос ({@link #socketReaderThread}) для чтения входящих сообщений
 *
 * <hr>
 * <h2>Протокол</h2>
 * TcpClient оперирует понятием сообщения {@link Message}.
 *
 * <p> Сообщение -
 * это объект который серилизован стандартным механизмом сериализации
 * Java {@link java.io.ObjectOutputStream}
 *
 * <p> Сообщения передаются в обе стороны
 *
 * <p>
 * Клиент хранит счетчик сообщений ({@link TcpProtocol#sid}) на основании которого ведет свою уникальность идентификаторов.
 * Данный счет увеличивает свои номера линейно вверх, без повторений.
 *
 * <p> При посылке сообщения на сервер
 * {@link TcpProtocol#send(xyz.cofe.trambda.tcp.Message, java.util.function.Consumer, xyz.cofe.trambda.tcp.HeaderValue...)}
 * К каждому сообщению прикрепляется уникальный номер и дополнительная карта (ключ/значение) связанное с сообщением.
 *
 * <p> Так же сервер отвечает на это сообщение.
 * Сервер вместе с ответным сообщением может ссылаться на исходное сообщение - то на что ответ приходиться.
 *
 * <p> Пример посылки сообщения
 *
 * <table>
 * <tr>
 * <th>Клиент</th>
 * <th>Сервер</th>
 * </tr>
 *
 * <tr>
 * <td>
 * 1. увеличивает счетчик sid на 1
 * <br> {@code sid++}
 *
 * <br> 2. текущее значение sid сохраняет в msgId:
 * <br>{@code msgId = sid}
 *
 * <br> 3. сериализует сообщения
 * <br> {@code byte[] payload = serialize(msg)}
 *
 * <br> 4. формирует заголовок {@link TcpHeader}
 * <br> 4.1 В заголовке указывает тип сообщения
 * <br> {@code header.method = msg.getClass().getSimpleName()}
 * <br> 4.2 В заголовке указывает идентификатор сообщения
 * <br> {@code header,msgId = msgId}
 * <br> 4.3 В заголовке указывает дополнительную информацию
 * <br> например md5, размер payload, ...
 *
 * <br> 5. сфорримированные данные склеивает и отсылает на сервер
 * <br> {@code byte[] tcpPackage = header + payload }
 *
 * </td>
 * <td></td>
 * </tr>
 *
 * <tr>
 * <td></td>
 * <td>
 * 6. Принимает заголовок, из которого узнает размер полезных данных
 * <br> {@code payloadSize = payloadSizeOf(tcpPackage)}
 * <br> {@code headerSize = headerSizeOf(tcpPackage)}
 *
 * <br> 7. Считывает недостающие данные в буфер
 * <pre>
 * while(readed &lt; payloadSize+headerSize) {
 *   readed += read( buffer )
 * }
 * </pre>
 *
 * 8. Восстаналивает из буфера сообщение
 * и выполняет полезное действие связанное с сообщением
 * <br> 9. Если в результате действий есть результат который
 * надо отправить клиенту, то формируется ответное сообщение
 * по аналогии как формирует клиент, и отправляется обратно клиенту.
 * </pre>
 *
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * 10. Фоновый поток принимает ответное сообщение
 * и восстанавливает его {@link TcpProtocol#readNow}, так же как сервер
 *
 * <br> 11. Если с данным ответом были связаны подписчики,
 * то вызывает их {@link TcpProtocol#getResponseConsumers()}
 * </td>
 * <td>
 * </td>
 * </tr>
 * </table>
 *
 * <p> За связку конкретного запроса
 * и ответа на него в синхронном/асинхронном режиме
 * на клиенте отвечает класс {@link ResultConsumer}
 *
 * <p> За структуру TCP сообщения следует смотреть
 * класс {@link TcpHeader}
 */
public class TcpClient implements AutoCloseable {
    private static final Logger log = Logger.of(TcpClient.class);

    /**
     * Клиентский сокет
     */
    protected final Socket socket;

    /**
     * Работа с клиентским сокетом
     */
    protected final TcpProtocol proto;

    /**
     * Поток читающих входящие сообщения
     */
    protected final Thread socketReaderThread;

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

    /**
     * Конструктор
     * <br>
     * создает ({@link #createThread(Runnable, Socket)}) и запускает поток для чтения входящих сообщений {@link Message}.
     * @param socket клиентский сокет
     */
    public TcpClient(Socket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;
        this.proto = new TcpProtocol(socket);
        var th = createThread(this::reader, socket);
        if( th==null )throw new IllegalStateException("!bug createThread() return null");
        socketReaderThread = th;
        socketReaderThread.start();
    }

    /**
     * Создание потока для чтения входящих сообщений
     * @param code код читающий входящие сообщения
     * @param socket сокет клиента
     * @return поток ос
     */
    protected Thread createThread( Runnable code, Socket socket ){
        var th = new Thread(this::reader);
        th.setDaemon(true);
        th.setName("client "+socket.getRemoteSocketAddress());
        return th;
    }

    /**
     * Завершение работы клиента, вызывает {@link #shutdown()}
     */
    @Override
    public synchronized void close() {
        shutdown();
    }

    /**
     * Завершение работы клиента
     *
     * <br>
     * Нельзя вызывать из того же потока, который осуществляет чтение входящих сообщений - {@link #socketReaderThread}
     */
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

    /**
     * Компиляция лямбды
     * <p> см. {@link Compile}, {@link CompileResult}, {@link TcpClient#compile(LambdaDump)}, {@link AsmQuery см call(Fn1, SerializedLambda, LambdaDump)} ()}
     * @param methodDef лямбда
     * @return выполнение запроса
     */
    public ResultConsumer<Compile,CompileResult> compile(LambdaDump methodDef){
        if( methodDef==null )throw new IllegalArgumentException( "methodDef==null" );
        return proto.compile(methodDef);
    }
    //endregion
    //region execute()

    /**
     * Выполнение ранее скомпилированной лямбды ({@link #compile(LambdaDump)})
     * <p> см. {@link Compile}, {@link CompileResult}, {@link TcpClient#compile(LambdaDump)}, {@link AsmQuery см call(Fn1, SerializedLambda, LambdaDump)} ()}
     * @param cres результат компиляции
     * @return выполнение запроса
     */
    public ResultConsumer<Execute,ExecuteResult> execute(CompileResult cres){
        if( cres==null )throw new IllegalArgumentException( "cres==null" );
        return proto.execute(cres);
    }
    //endregion
    //region subscribe()

    /**
     * Список подписчиков на события сервера
     */
    protected final Map<String, List<Tuple2<Consumer<ServerEvent>,AutoCloseable>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Подписка на события сервера
     * @param subscribe подписка (имя издателя)
     * @param listener подписчик
     * @return выполнение запроса
     */
    public ResultConsumer<Subscribe, SubscribeResult> subscribe(Subscribe subscribe, Consumer<ServerEvent> listener){
        if( subscribe==null )throw new IllegalArgumentException( "subscribe==null" );

        var pubName = subscribe.getPublisher();
        if( pubName==null )throw new IllegalArgumentException( "subscribe.pubName==null" );

        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return proto.subscribe(subscribe).onSuccess( m -> {
            log.info("subscribe publisher={}",pubName);
            log.debug("proto.listenServerEvent");
            var cl = proto.listenServerEvent(pubName, listener);
            synchronized( subscribers ){
                subscribers.computeIfAbsent(pubName, x -> new CopyOnWriteArrayList<>()).add(
                    Tuple2.of(listener,cl)
                );
            }
        });
    }
    public ResultConsumer<Subscribe, SubscribeResult> subscribe(String publisher, Consumer<ServerEvent> listener){
        if( publisher==null )throw new IllegalArgumentException( "publisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        Subscribe subscribe = new Subscribe();
        subscribe.setPublisher(publisher);
        return subscribe(subscribe,listener);
    }
    public ResultConsumer<UnSubscribe, UnSubscribeResult> unsubscribe(UnSubscribe subscribe){
        if( subscribe==null )throw new IllegalArgumentException( "subscribe==null" );

        var pubName = subscribe.getPublisher();
        if( pubName==null )throw new IllegalArgumentException( "subscribe.pubName==null" );

        return proto.unsubscribe(subscribe).onSuccess( m -> {
            log.info("unsubscribed publisher={}",pubName);
            log.debug("proto.listenServerEvent.close all");
            synchronized( subscribers ){
                var l = subscribers.get(pubName);
                if( l != null ){
                    for( var c : l ){
                        if( c != null ){
                            try{
                                c.b().close();
                            } catch( Exception e ) {
                                log.error("remove listener proto.listenServerEvent error", e);
                            }
                        }
                    }
                    l.clear();
                }
                subscribers.remove(pubName);
            }
        });
    }
    public ResultConsumer<UnSubscribe, UnSubscribeResult> unsubscribe(String publisher){
        if( publisher==null )throw new IllegalArgumentException( "publisher==null" );
        var subscribe = new UnSubscribe();
        subscribe.setPublisher(publisher);
        return unsubscribe(subscribe);
    }
    public void unsubscribe( Consumer<? super ServerEvent> listener ){
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        synchronized( subscribers ){
            var unsubPubs = new HashSet<String>();
            for( var pub : subscribers.keySet() ){
                var ls = subscribers.get(pub);
                var lsRemItems = new ArrayList<>();
                for( var e : ls ){
                    if( e.a()==listener ){
                        try{
                            e.b().close();
                        } catch( Exception exception ) {
                            log.error("unsubscribe {}", listener);
                        }
                        lsRemItems.add(e);
                    }
                }
                //noinspection SuspiciousMethodCalls
                lsRemItems.forEach(ls::remove);
                if( ls.isEmpty() ){
                    unsubPubs.add(pub);
                }
            }
            for( var unsubPub : unsubPubs ){
                unsubscribe(unsubPub).fetch();
            }
        }
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
