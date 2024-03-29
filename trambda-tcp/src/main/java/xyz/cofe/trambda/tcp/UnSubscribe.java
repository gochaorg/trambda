package xyz.cofe.trambda.tcp;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Отписка от событий сервера
 *
 * <p>
 * см:
 * <ul>
 *     <li>{@link Subscribe}</li>
 *     <li>{@link SubscribeResult}</li>
 *     <li>{@link UnSubscribe}</li>
 *     <li>{@link TcpClient#subscribe(String, Consumer)}</li>
 *     <li>{@link TcpQuery#subscribe(Class, Consumer)}</li>
 * </ul>
 */
public class UnSubscribe implements Serializable, Message {
    /**
     * Конструктор
     */
    public UnSubscribe(){}

    /**
     * Конструктор
     * @param publisher имя издателя, см {@link TcpServer#publisher(String)}, {@link TcpServer#publishers(Class)}
     */
    public UnSubscribe(String publisher){
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
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<UnSubscribe> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof UnSubscribe ){
                    listener.accept((UnSubscribe) msg);
                }
            }
        });
    }

}
