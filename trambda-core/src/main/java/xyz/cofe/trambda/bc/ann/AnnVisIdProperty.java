package xyz.cofe.trambda.bc.ann;

import xyz.cofe.trambda.bc.ByteCode;

public interface AnnVisIdProperty extends ByteCode {
    public static final int DEF_ANNOTATION_VISITOR_ID = -1;
    default int getAnnotationVisitorId(){ return DEF_ANNOTATION_VISITOR_ID; };
    void setAnnotationVisitorId(int id);
}
