package xyz.cofe.trambda.tcp;

import java.io.Serializable;
import xyz.cofe.ecolls.ListenersHelper;

/**
 * "Издатель" серверных событий
 * @param <T> тип серверного события
 */
public class Publisher<T extends Serializable> {
    public void publish(T msg){
        if( msg==null )throw new IllegalArgumentException( "msg==null" );
        listenersHelper.fireEvent(msg);
    }

    /**
     * Интерейс уведомления о серверном событии
     * @param <T> тип серверного события
     */
    public interface Subscriber<T> {
        void notification(T ev);
    }

    private final ListenersHelper<Subscriber<T>,T> listenersHelper =
        new ListenersHelper<>(Subscriber::notification);

    /**
     * Подписка на серверные события, вызывается в клиентском коде
     * @param subscriber подписчик
     * @return отписка от уведомлений сервера
     */
    public AutoCloseable listen( Subscriber<T> subscriber ){
        if( subscriber==null )throw new IllegalArgumentException( "subscriber==null" );
        return listenersHelper.addListener(subscriber);
    }

//    public AutoCloseable listen(boolean weak, Subscriber<T> subscriber ){
//        if( subscriber==null )throw new IllegalArgumentException( "subscriber==null" );
//        return listenersHelper.addListener(subscriber,weak);
//    }

    /**
     * Отписка от серверных событий
     * @param subscriber подписчик
     */
    public void removeListener( Subscriber<T> subscriber ){
        listenersHelper.removeListener(subscriber);
    }
}
