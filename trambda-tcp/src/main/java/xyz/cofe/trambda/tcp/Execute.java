package xyz.cofe.trambda.tcp;

import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.LambdaDump;

import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.function.Consumer;

/**
 * Запрос выполнения скомпилированной лямбды,
 * Предварительно лямбда должны быть скомпилирована:
 * {@link Compile}, {@link CompileResult}, {@link TcpClient#compile(LambdaDump)}, {@link AsmQuery см call(Fn1, SerializedLambda, LambdaDump)} ()}
 */
public class Execute implements Message {
    //region key : Integer
    private Integer key;

    /**
     * Возвращает ключ-идентификатор лямбды, см {@link Compile}, {@link CompileResult}
     * @return ключ идентификатор лямбды
     */
    public Integer getKey(){
        return key;
    }

    /**
     * Указывает ключ-идентификатор лямбды, см {@link Compile}, {@link CompileResult}
     * @param key ключ идентификатор лямбды
     */
    public void setKey(Integer key){
        this.key = key;
    }
    //endregion
    //region hash : String
    private String hash;

    /**
     * Возвращает хеш лямбды, см {@link Compile}, {@link CompileResult}
     * @return хеш лямбды
     */
    public String getHash(){
        return hash;
    }

    /**
     * Указывает хеш лямбды, см {@link Compile}, {@link CompileResult}
     * @param hash хеш лямбды
     */
    public void setHash(String hash){
        this.hash = hash;
    }
    //endregion
    //region capturedArgs : List
    private List<Object> capturedArgs;

    /**
     * Возвращает захваченные параметры
     * @return захваченные параметры
     */
    public List<Object> getCapturedArgs(){ return capturedArgs; }

    /**
     * Указывает захваченные параметры
     * @param capturedArgs захваченные параметры
     */
    public void setCapturedArgs(List<Object> capturedArgs){ this.capturedArgs = capturedArgs; }
    //endregion

    public String toString(){
        return "Execute key="+key+" hash="+hash+" args="+capturedArgs;
    }

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<Execute> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof Execute ){
                    listener.accept((Execute) msg);
                }
            }
        });
    }

}
