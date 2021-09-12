package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.tcp.Message;

import java.util.function.Consumer;

/**
 * Результат компиляции {@link Compile}
 */
public class CompileResult implements Message {
    //region key : Integer - идентификатор единицы компиляции
    private Integer key;
    /**
     * Возвращает идентификатор единицы компиляции
     * @return идентификатор единицы компиляции
     */
    public Integer getKey(){ return key; }
    /**
     * Указывает идентификатор единицы компиляции
     * @param key идентификатор единицы компиляции
     */
    public void setKey(Integer key){ this.key = key; }
    //endregion
    //region hash : String - хеш код
    private String hash;
    
    /**
     * Возвращает хеш код
     * @return хеш код
     */
    public String getHash(){
        return hash;
    }

    /**
     * Указывает хеш код
     * @param hash хеш код
     */
    public void setHash(String hash){
        this.hash = hash;
    }
    //endregion

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<CompileResult> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof CompileResult ){
                    listener.accept((CompileResult) msg);
                }
            }
        });
    }
}
