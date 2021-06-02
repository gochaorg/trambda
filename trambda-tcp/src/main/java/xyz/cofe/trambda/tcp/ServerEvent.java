package xyz.cofe.trambda.tcp;

import java.io.Serializable;

public class ServerEvent implements Message {
    private String publisher;
    public String getPublisher(){ return publisher; }
    public void setPublisher(String v){ publisher = v; }

    private Serializable event;
    public Serializable getEvent(){ return event; }
    public void setEvent(Serializable event){ this.event = event; }

    public String toString(){
        return "ServerEvent{ publisher="+publisher+" event="+event+" }";
    }
}
