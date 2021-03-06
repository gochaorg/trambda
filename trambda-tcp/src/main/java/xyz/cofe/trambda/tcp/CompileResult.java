package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.tcp.Message;

public class CompileResult implements Message {
    //region key : Integer
    private Integer key;
    public Integer getKey(){ return key; }
    public void setKey(Integer key){ this.key = key; }
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
}
