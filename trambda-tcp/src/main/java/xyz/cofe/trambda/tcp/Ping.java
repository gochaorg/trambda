package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.tcp.Message;

import java.util.function.Consumer;

/**
 * Пинг запрос, ожидается ответ на него {@link Pong}
 */
public class Ping implements Message {
    private static final long serialVersionUID = 4L;
    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<Ping> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof Ping ){
                    listener.accept((Ping) msg);
                }
            }
        });
    }

}
