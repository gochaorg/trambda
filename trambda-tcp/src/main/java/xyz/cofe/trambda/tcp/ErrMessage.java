package xyz.cofe.trambda.tcp;

public class ErrMessage implements Message {
    private String message;
    public String getMessage(){ return message; }
    public void setMessage(String message){ this.message = message; }
    public ErrMessage message(String message){
        this.message = message;
        return this;
    }
    public ErrMessage error(Throwable err){
        return this;
    }
}
