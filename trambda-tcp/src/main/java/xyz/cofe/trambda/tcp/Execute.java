package xyz.cofe.trambda.tcp;

import java.util.List;

public class Execute implements Message {
    //region key : Integer
    private Integer key;
    public Integer getKey(){
        return key;
    }
    public void setKey(Integer key){
        this.key = key;
    }
    //endregion
    //region hash : String
    private String hash;
    public String getHash(){
        return hash;
    }
    public void setHash(String hash){
        this.hash = hash;
    }
    //endregion
    //region capturedArgs : List
    private List<Object> capturedArgs;
    public List<Object> getCapturedArgs(){ return capturedArgs; }
    public void setCapturedArgs(List<Object> capturedArgs){ this.capturedArgs = capturedArgs; }
    //endregion

    public String toString(){
        return "Execute key="+key+" hash="+hash+" args="+capturedArgs;
    }
}
