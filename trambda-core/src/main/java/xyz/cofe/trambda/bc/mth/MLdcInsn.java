package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.bm.LdcType;

public class MLdcInsn extends MAbstractBC implements ByteCode {
    public MLdcInsn(){
    }
    public MLdcInsn(Object value, LdcType ldcType){
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
