package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;

public class CPermittedSubclass implements ClsByteCode {
    private static final long serialVersionUID = 1;
    public CPermittedSubclass(){}

    public CPermittedSubclass(String permittedSubclass){
        this.permittedSubclass = permittedSubclass;
    }

    protected String permittedSubclass;

    public String getPermittedSubclass(){
        return permittedSubclass;
    }

    public void setPermittedSubclass(String permittedSubclass){
        this.permittedSubclass = permittedSubclass;
    }

    @Override
    public String toString(){
        return "CPermittedSubclass " +
            "permittedSubclass='" + permittedSubclass + '\'';
    }
}
