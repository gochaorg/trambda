package xyz.cofe.trambda.tcp;

import java.io.Serializable;
import xyz.cofe.ecolls.ListenersHelper;

public class Publisher<T extends Serializable> {
    public void publish(T msg){
        if( msg==null )throw new IllegalArgumentException( "msg==null" );
        listenersHelper.fireEvent(msg);
    }

    public interface Subscriber<T> {
        void notification(T ev);
    }

    private final ListenersHelper<Subscriber<T>,T> listenersHelper =
        new ListenersHelper<>(Subscriber::notification);

    public AutoCloseable addListen( Subscriber<T> subscriber ){
        if( subscriber==null )throw new IllegalArgumentException( "subscriber==null" );
        return listenersHelper.addListener(subscriber);
    }

    public AutoCloseable addListen( boolean weak, Subscriber<T> subscriber ){
        if( subscriber==null )throw new IllegalArgumentException( "subscriber==null" );
        return listenersHelper.addListener(subscriber,weak);
    }

    public void removeListener( Subscriber<T> subscriber ){
        listenersHelper.removeListener(subscriber);
    }
}
