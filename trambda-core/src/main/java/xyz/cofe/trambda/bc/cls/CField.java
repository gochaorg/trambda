package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.fld.FldVisIdProperty;

public class CField implements ClsByteCode, FldVisIdProperty {
    private static final long serialVersionUID = 1;
    public CField(){}

    public CField(int access, String name, String descriptor, String signature, Object value){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
    }

    //region fieldVisitorId
    protected int fieldVisitorId = FldVisIdProperty.DEF_FIELD_VISITOR_ID;
    @Override
    public int getFieldVisitorId(){
        return fieldVisitorId;
    }

    @Override
    public void setFieldVisitorId(int id){
        fieldVisitorId = id;
    }
    //endregion

    //region access
    protected int access;
    public int getAccess(){
        return access;
    }

    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name
    protected String name;
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor
    protected String descriptor;
    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region signature
    protected String signature;
    public String getSignature(){
        return signature;
    }

    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region value
    protected Object value;

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }
    //endregion

    @Override
    public String toString(){
        return "CField" +
            " access=" + access +
            ", name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            ", signature='" + signature + '\'' +
            ", value=" + value ;
    }
}
