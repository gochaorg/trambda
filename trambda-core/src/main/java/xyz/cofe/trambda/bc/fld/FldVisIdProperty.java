package xyz.cofe.trambda.bc.fld;

public interface FldVisIdProperty {
    public static final int DEF_FIELD_VISITOR_ID = -1;
    default int getFieldVisitorId(){ return DEF_FIELD_VISITOR_ID; }
    void setFieldVisitorId(int id);
}
