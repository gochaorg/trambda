package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.mth.MAbstractBC;

public class CAnnotation implements ClsByteCode, AnnVisIdProperty, AnnotationDef {
    private static final long serialVersionUID = 1;

    public CAnnotation(){}
    public CAnnotation(String descriptor, boolean visible){
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
        return CAnnotation.class.getSimpleName()+" descriptor="+descriptor+" visible="+visible+" ann.v.id="+annotationVisitorId;
    }
}
