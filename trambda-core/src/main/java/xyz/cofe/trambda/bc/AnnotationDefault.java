package xyz.cofe.trambda.bc;

import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;

public class AnnotationDefault implements ByteCode, AnnVisIdProperty {
    private static final long serialVersionUID = 1;

    public AnnotationDefault(){}
    public AnnotationDefault(int annotationVisitorId){
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
