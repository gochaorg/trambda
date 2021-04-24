package xyz.cofe.trambda.bc.mth;

public interface MthVisIdProperty {
    public static final int DEF_METHOD_VISITOR_ID = -1;
    default int getMethodVisitorId(){ return DEF_METHOD_VISITOR_ID; };
    void setMethodVisitorId(int id);
}
