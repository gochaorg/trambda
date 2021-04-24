package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

public abstract class MAbstractBC implements MethodByteCode {
    protected int methodVisitorId = MthVisIdProperty.DEF_METHOD_VISITOR_ID;

    @Override
    public int getMethodVisitorId(){
        return methodVisitorId;
    }

    @Override
    public void setMethodVisitorId(int id){
        methodVisitorId = id;
    }
}
