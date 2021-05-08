package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;

public class CNestHost implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public CNestHost(){}

    public CNestHost(String nestHost){
        this.nestHost = nestHost;
    }

    //region nestHost : String
    protected String nestHost;

    public String getNestHost(){
        return nestHost;
    }

    public void setNestHost(String nestHost){
        this.nestHost = nestHost;
    }
    //endregion

    public String toString(){ return CNestHost.class.getSimpleName()+" "+nestHost; }
}
