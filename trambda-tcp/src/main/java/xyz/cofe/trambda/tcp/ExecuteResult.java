package xyz.cofe.trambda.tcp;

import java.util.function.Consumer;

/**
 * Результат выполнения {@link Execute}
 */
public class ExecuteResult implements Message {
    //region started : long
    private long started;

    /**
     * Возвращает время начала выполнения
     * @return время начала выполнения
     */
    public long getStarted(){
        return started;
    }

    /**
     * Указывает время начала выполнения
     * @param started время начала выполнения
     */
    public void setStarted(long started){
        this.started = started;
    }
    //endregion
    //region startedNano : long
    private long startedNano;

    /**
     * Возвращает время начала выполнения (нс)
     * @return время начала выполнения (нс)
     */
    public long getStartedNano(){
        return startedNano;
    }

    /**
     * Указывает время начала выполнения (нс)
     * @param startedNano время начала выполнения (нс)
     */
    public void setStartedNano(long startedNano){
        this.startedNano = startedNano;
    }
    //endregion
    //region finished : long
    private long finished;

    /**
     * Возвращает время завершения выполнения
     * @return время завершения выполнения
     */
    public long getFinished(){
        return finished;
    }

    /**
     * Указывает время завершения выполнения
     * @param finished время завершения выполнения
     */
    public void setFinished(long finished){
        this.finished = finished;
    }
    //endregion
    //region finishedNano : long
    private long finishedNano;

    /**
     * Возвращает время завершения выполнения (нс)
     * @return время завершения выполнения
     */
    public long getFinishedNano(){
        return finishedNano;
    }

    /**
     * Указывает время завершения выполнения (нс)
     * @param finishedNano время завершения выполнения
     */
    public void setFinishedNano(long finishedNano){
        this.finishedNano = finishedNano;
    }
    //endregion
    //region value : Object
    private Object value;

    /**
     * Возвращает результат выполнения
     * @return результат выполнения
     */
    public Object getValue(){
        return value;
    }

    /**
     * Указывает результат выполнения
     * @param value результат выполнения
     */
    public void setValue(Object value){
        this.value = value;
    }
    //endregion

    /**
     * Подписка на события Compile
     * @param evPublisher издатель событий
     * @param listener подписчик
     * @return отписка от событий
     */
    public static AutoCloseable listen(TrEventPublisher evPublisher, Consumer<ExecuteResult> listener ){
        if( evPublisher==null )throw new IllegalArgumentException( "evPublisher==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return evPublisher.addListener( ev -> {
            if( ev instanceof TcpSession.MessageEvent ){
                var msg = (((TcpSession.MessageEvent<?, ?>) ev).message);
                if( msg instanceof ExecuteResult ){
                    listener.accept((ExecuteResult) msg);
                }
            }
        });
    }
}
