package xyz.cofe.trambda.tcp;

import java.io.Serializable;

public class Subscribe implements Serializable, Message {
    public Subscribe(){}
    public Subscribe(String publisher){
        this.publisher = publisher;
    }

    private String publisher;
    public String getPublisher(){ return publisher; }
    public void setPublisher(String publisher){ this.publisher = publisher; }
}
