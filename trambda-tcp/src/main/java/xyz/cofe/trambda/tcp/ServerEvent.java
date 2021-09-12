package xyz.cofe.trambda.tcp;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * <p>Событие сервера
 * <p> см {@link Subscribe}
 */
public class ServerEvent implements Message {
    private String publisher;

    /**
     * Возвращает {@link TcpServer#publisher(String) имя } {@link Publisher издателя}
     * @return имя издателя
     */
    public String getPublisher(){ return publisher; }

    /**
     * Указывает {@link TcpServer#publisher(String) имя } {@link Publisher издателя}
     * @param v имя издателя
     */
    public void setPublisher(String v){ publisher = v; }

    private Serializable event;

    /**
     * Возвращает событие издателя
     * @return событие издателя
     */
    public Serializable getEvent(){ return event; }

    /**
     * Указывает событие издателя
     * @param event событие издателя
     */
    public void setEvent(Serializable event){ this.event = event; }

    public String toString(){
        return "ServerEvent{ publisher="+publisher+" event="+event+" }";
    }

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<ServerEvent> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof ServerEvent ){
                    listener.accept((ServerEvent) msg);
                }
            }
        });
    }

}
