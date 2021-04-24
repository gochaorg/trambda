package xyz.cofe.trambda.bc.ann;

public abstract class AAbstractBC implements AnnVisIdProperty, AnnByteCode {
    protected int annotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){ return annotationVisitorId; };
    public void setAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    }
}
