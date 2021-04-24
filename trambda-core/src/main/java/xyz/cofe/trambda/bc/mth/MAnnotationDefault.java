package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;

public class MAnnotationDefault extends MAbstractBC implements ByteCode, AnnVisIdProperty {
    private static final long serialVersionUID = 1;

    public MAnnotationDefault(){}
    public MAnnotationDefault(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }

    private int annotationVisitorId = DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){
        return annotationVisitorId;
    }
    public void setAnnotationVisitorId(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }

    public String toString(){
        return "AnnotationDefault visitorId="+annotationVisitorId;
    }
}
