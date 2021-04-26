package xyz.cofe.trambda.bc.ann;

public abstract class EmbededAnnotation extends AAbstractBC implements AnnotationDef<EmbededAnnotation> {
    protected int embededAnnotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getEmbededAnnotationVisitorId(){ return annotationVisitorId; };
    public void setEmbededAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    };

    @Override
    public int getAnnotationDefVisitorId(){ return getEmbededAnnotationVisitorId(); }
}
