package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;

public class CSource implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public CSource(){
    }
    public CSource(String source, String debug){
        this.source = source;
        this.debug = debug;
    }

    protected String source;
    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    protected String debug;

    public String getDebug(){
        return debug;
    }

    public void setDebug(String debug){
        this.debug = debug;
    }

    @Override
    public String toString(){
        return "ClassSource " +
            "source='" + source + '\'' +
            ", debug='" + debug + '\'' ;
    }
}
