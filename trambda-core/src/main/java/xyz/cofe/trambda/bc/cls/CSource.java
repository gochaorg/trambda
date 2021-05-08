package xyz.cofe.trambda.bc.cls;

public class CSource implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public CSource(){
    }
    public CSource(String source, String debug){
        this.source = source;
        this.debug = debug;
    }

    //region source : String
    protected String source;
    public String getSource(){
        return source;
    }
    public void setSource(String source){
        this.source = source;
    }
    //endregion
    //region debug : String
    protected String debug;

    public String getDebug(){
        return debug;
    }

    public void setDebug(String debug){
        this.debug = debug;
    }
    //endregion

    @Override
    public String toString(){
        return CSource.class.getSimpleName()+" " +
            "source=" + (source!=null ? "\""+source+"\"" : "null") +
            ", debug=" + (debug!=null ? "\""+debug+"\"" : "null");
    }
}
