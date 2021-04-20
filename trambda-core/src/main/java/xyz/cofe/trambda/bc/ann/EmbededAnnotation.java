package xyz.cofe.trambda.bc.ann;

public abstract class EmbededAnnotation extends AnnAbstractBC {
    protected int embededAnnotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getEmbededAnnotationVisitorId(){ return annotationVisitorId; };
    public void setEmbededAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    };
}
