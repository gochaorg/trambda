package xyz.cofe.trambda.tcp.demo;

import java.io.Serializable;

public class ServerDemoEvent2 implements Serializable {
    public String message;
    public long serverTime = System.currentTimeMillis();

    public ServerDemoEvent2(){ }
    public ServerDemoEvent2(String msg){
        this.message = msg;
    }
    public ServerDemoEvent2(String msg, long serverTime){
        this.message = msg;
        this.serverTime = serverTime;
    }
}
