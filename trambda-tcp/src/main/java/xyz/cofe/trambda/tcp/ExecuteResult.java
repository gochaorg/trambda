package xyz.cofe.trambda.tcp;

public class ExecuteResult implements Message {
    //region started : long
    private long started;
    public long getStarted(){
        return started;
    }

    public void setStarted(long started){
        this.started = started;
    }
    //endregion

    //region startedNano : long
    private long startedNano;

    public long getStartedNano(){
        return startedNano;
    }

    public void setStartedNano(long startedNano){
        this.startedNano = startedNano;
    }
    //endregion

    //region finished : long
    private long finished;
    public long getFinished(){
        return finished;
    }

    public void setFinished(long finished){
        this.finished = finished;
    }
    //endregion

    //region finishedNano : long
    private long finishedNano;

    public long getFinishedNano(){
        return finishedNano;
    }

    public void setFinishedNano(long finishedNano){
        this.finishedNano = finishedNano;
    }
    //endregion

    //region value : Object
    private Object value;

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }
    //endregion
}
