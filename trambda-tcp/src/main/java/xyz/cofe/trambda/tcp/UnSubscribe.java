package xyz.cofe.trambda.tcp;

import java.io.Serializable;

public class UnSubscribe implements Serializable, Message {
    public UnSubscribe(){}
    public UnSubscribe(String publisher){
        this.publisher = publisher;
    }

    private String publisher;
    public String getPublisher(){ return publisher; }
    public void setPublisher(String publisher){ this.publisher = publisher; }
}
