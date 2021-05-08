package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;

public class MParameter extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MParameter(){}
    public MParameter(String name, int access){
        this.access = access;
        this.name = name;
    }

    //region access : int
    private int access;

    public int getAccess(){
        return access;
    }

    public void setAccess(int acc){
        this.access = acc;
    }
    //endregion
    //region name : String
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion

    public String toString(){
        return MParameter.class.getSimpleName()+
            " name="+name+
            " access="+access+"#"+new AccFlags(access).flags()
            ;
    }
}
