package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.LambdaDump;

import java.util.function.Consumer;

/**
 * Запрос компиляции лямбды
 */
public class Compile implements Message {
    private LambdaDump dump;

    /**
     * Возвращает дамп лямбды
     * @return лямбда
     */
    public LambdaDump getDump(){
        return dump;
    }

    /**
     * Указывает дамп лямбды
     * @param dump лямбда
     */
    public void setDump(LambdaDump dump){
        this.dump = dump;
    }

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<Compile> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof Compile ){
                    listener.accept((Compile) msg);
                }
            }
        });
    }
}
