package xyz.cofe.trchat;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    public ChatMessage(){}
    public ChatMessage(String user, String message){
        this.user = user;
        this.message = message;
    }

    //region user : String
    private String user;

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }
    //endregion
    //region message : String
    private String message;

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }
    //endregion
}
