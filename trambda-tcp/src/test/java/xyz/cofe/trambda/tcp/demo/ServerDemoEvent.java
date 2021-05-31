package xyz.cofe.trambda.tcp.demo;

import java.io.Serializable;

public class ServerDemoEvent implements Serializable {
    public String message;
    public ServerDemoEvent(){
    }
    public ServerDemoEvent(String msg){
        this.message = msg;
    }
}
