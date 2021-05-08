package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;

public class CNestMember implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public CNestMember(){}

    public CNestMember(String nestMember){
        this.nestMember = nestMember;
    }

    //region nestMember : String
    protected String nestMember;

    public String getNestMember(){
        return nestMember;
    }

    public void setNestMember(String nestMember){
        this.nestMember = nestMember;
    }
    //endregion

    @Override
    public String toString(){
        return CNestMember.class.getSimpleName()+" " +
            "nestMember='" + nestMember + '\'' ;
    }
}
