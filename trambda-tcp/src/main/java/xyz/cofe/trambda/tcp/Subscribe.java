package xyz.cofe.trambda.tcp;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * <p>Подписка на уведомления сервера</p>
 * <h2>Подписка на уведомления сервера</h2>
 * 
 * Tcp сервер содержит метод {@link TcpServer#publisher(java.lang.String) publisher()} 
 * для создания "издателя".
 * 
 * <p> Издатель создается на сервере, а на клиенте предоставляется Proxy для этого издателя.
 * 
 * <p> События генерируемые сервером рассылается в каждую {@link TcpServer#getSessions() сессию сервера}
 * <br> Если клиент {@link TcpClient#subscribe(java.lang.String, java.util.function.Consumer) подписался на события},
 * тогда сессия сервера передаст в сокет пакет с сообщением(событием)
 * 
 * <p> Пример: 
 * 
 * <p> Определим сам состав событий
 * <pre>
import java.io.Serializable;

public class ServerDemoEvent implements Serializable {
    public String message;
    public ServerDemoEvent(){
    }
    public ServerDemoEvent(String msg){
        this.message = msg;
    }
}

public class ServerDemoEvent2 implements Serializable {
    public String message;
    public long serverTime = System.currentTimeMillis();

    public ServerDemoEvent2(){ }
    public ServerDemoEvent2(String msg){
        this.message = msg;
    }
    public ServerDemoEvent2(String msg, long serverTime){
        this.message = msg;
        this.serverTime = serverTime;
    }
}
* </pre>
 * 
 * <p> Создаем интерфейс для перелачи событий
 * <pre>
public interface Events {
    Publisher&lt;ServerDemoEvent&gt; defaultPublisher();
    Publisher&lt;ServerDemoEvent2&gt; timedEvents();
}
 * </pre>
 * 
 * <p> Передавать события из сервиса будем так:
 * <pre>
 * // Получаем ссылку на передачу сообщений
 * Publisher&lt;ServerDemoEvent&gt; pub = ...
 * ...
 * 
 * // Передаем событие подписчикам
 * pub.publish( new ServerDemoEvent("message#"+i) );
 * </pre>
 * 
 * <p> На сервере, в момент создания сессии (или другой удобный момент)
 * получаем ссылку на Events:
 * 
 * <pre>
 * server = new TcpServer&lt;IEnv&gt;(ssocket,
        session -gt; new LinuxEnv(
            // метод {@link TcpServer#publishers(java.lang.Class) publishers(itf)} - возвращает прокси 
            // через котторый мы получаем реализацию 
            // Publisher&lt;ServerDemoEvent&gt;
            // или другой, например
            // Publisher&lt;ServerDemoEvent2&gt;
            // 
            // В конструктор LinuxEnv( Events events )
            // есть примерно такой код:
            // LinuxEnv( Events events ){
            //   ...
            //   pub = events.defaultPublisher();
            // }
            session.getServer().publishers(Events.class)            
        )
    );
 * </pre>
 * 
 * <h3>На клиенте</h3>
 * 
 * <pre>
 * // Создаем клиента
 * var query = TcpQuery.create(IEnv.class)
 *   .host("localhost").port(port+1).build();
 * 
 * // Подписываемся на события
 * query.subscribe(Events.class, pubs -&gt; {
 *   pubs.defaultPublisher().listen( msg -&gt; {
 *     System.out.println("message "+msg.message);
 *   });
 * 
 *   pubs.timedEvents().listen( msg -&gt; {
 *     System.out.println(
 *       "t.message "+msg.message+" t="+msg.serverTime);
 *   });
 * });

 * </pre>
 *
 * см:
 * <ul>
 *     <li>{@link SubscribeResult}</li>
 *     <li>{@link UnSubscribe}</li>
 *     <li>{@link TcpClient#subscribe(String, Consumer)}</li>
 *     <li>{@link TcpQuery#subscribe(Class, Consumer)}</li>
 * </ul>
 */
public class Subscribe implements Serializable, Message {
    /**
     * Конструктор
     */
    public Subscribe(){}

    /**
     * Конструктор
     * @param publisher имя издателя, см {@link TcpServer#publisher(String)}, {@link TcpServer#publishers(Class)}
     */
    public Subscribe(String publisher){
        this.publisher = publisher;
    }

    private String publisher;

    /**
     * Возвращает имя издателя, см {@link TcpServer#publisher(String)}, {@link TcpServer#publishers(Class)}
     * @return имя издателя
     */
    public String getPublisher(){ return publisher; }

    /**
     * Указывает имя издателя, см {@link TcpServer#publisher(String)}, {@link TcpServer#publishers(Class)}
     * @param publisher имя издателя
     */
    public void setPublisher(String publisher){ this.publisher = publisher; }

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<Subscribe> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof Subscribe ){
                    listener.accept((Subscribe) msg);
                }
            }
        });
    }

}
