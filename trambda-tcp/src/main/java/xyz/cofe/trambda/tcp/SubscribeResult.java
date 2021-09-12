package xyz.cofe.trambda.tcp;

import java.util.function.Consumer;

/**
 * Результат подписки (успешное) на событие сервера
 */
public class SubscribeResult implements Message {
    protected long subscribeTime;

    /**
     * Возвращает время начала подписки
     * @return время начала подписки
     */
    public long getSubscribeTime(){
        return subscribeTime;
    }

    /**
     * Указывает время начала подписки
     * @param subscribeTime время начала подписки
     */
    public void setSubscribeTime(long subscribeTime){
        this.subscribeTime = subscribeTime;
    }

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<SubscribeResult> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof SubscribeResult ){
                    listener.accept((SubscribeResult) msg);
                }
            }
        });
    }

}
