package xyz.cofe.trambda.bc;

public class LdcInsn implements ByteCode {
    public LdcInsn(){
    }
    public LdcInsn(Object value, LdcType ldcType){
        this.value = value;
        this.ldcType = ldcType;
    }
    //region ldcType : LdcType
    private LdcType ldcType;
    public LdcType getLdcType(){
        return ldcType;
    }

    public void setLdcType(LdcType ldcType){
        this.ldcType = ldcType;
    }
    //endregion
    //region value
    private Object value;

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }
    //endregion
    public String toString(){
        return "LdcInsn "+ldcType+" "+value;
    }
}
