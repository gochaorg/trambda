package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.tcp.Message;

public class CompileResult implements Message {
    private Integer key;
    public Integer getKey(){ return key; }
    public void setKey(Integer key){ this.key = key; }
}
