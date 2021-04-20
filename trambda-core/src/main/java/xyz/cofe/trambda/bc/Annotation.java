package xyz.cofe.trambda.bc;

import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;

public class Annotation implements ByteCode, AnnVisIdProperty {
    private static final long serialVersionUID = 1;

    public Annotation(){}
    public Annotation(String descriptor, boolean visible){
        this.descriptor = descriptor;
        this.visible = visible;
    }

    protected String descriptor;
    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }

    protected boolean visible;

    public boolean isVisible(){
        return visible;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    private int annotationVisitorId = DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){
        return annotationVisitorId;
    }
    public void setAnnotationVisitorId(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }

    public String toString(){
        return Annotation.class.getSimpleName()+" descriptor="+descriptor+" visible="+visible+" ann.v.id="+annotationVisitorId;
    }
}
