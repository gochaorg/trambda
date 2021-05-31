package xyz.cofe.trambda.tcp;

import java.io.Serializable;

public class ServerEvent implements Message {
    private Serializable event;
    public Serializable getEvent(){ return event; }
    public void setEvent(Serializable event){ this.event = event; }
}
