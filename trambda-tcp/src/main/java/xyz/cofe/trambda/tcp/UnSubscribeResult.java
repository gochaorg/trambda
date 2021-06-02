package xyz.cofe.trambda.tcp;

public class UnSubscribeResult implements Message {
    protected long subscribeTime;
    public long getSubscribeTime(){
        return subscribeTime;
    }
    public void setSubscribeTime(long subscribeTime){
        this.subscribeTime = subscribeTime;
    }
}
