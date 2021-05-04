package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class MAnnotation extends MAbstractBC implements ByteCode, AnnVisIdProperty, AnnotationDef {
    private static final long serialVersionUID = 1;

    public MAnnotation(){}
    public MAnnotation(String descriptor, boolean visible){
        this.descriptor = descriptor;
        this.visible = visible;
    }

    //region descriptor : String
    protected String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region visible : boolean
    protected boolean visible;
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
    }
    //endregion
    //region annotationVisitorId : int
    protected int annotationVisitorId = DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){
        return annotationVisitorId;
    }
    public void setAnnotationVisitorId(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }
    //endregion

    public String toString(){
        return MAnnotation.class.getSimpleName()+" descriptor="+descriptor+" visible="+visible+" ann.v.id="+annotationVisitorId;
    }
}
