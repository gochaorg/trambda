package xyz.cofe.trambda.bc.fld;

import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class FAnnotation implements FieldByteCode, AnnVisIdProperty, AnnotationDef {
    private static final long serialVersionUID = 1;

    public FAnnotation(){}
    public FAnnotation(String descriptor, boolean visible){
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
    private int annotationVisitorId = DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){
        return annotationVisitorId;
    }
    public void setAnnotationVisitorId(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }
    //endregion
    //region fieldVisitorId : int
    private int fieldVisitorId;
    @Override public int getFieldVisitorId(){
        return fieldVisitorId;
    }
    @Override public void setFieldVisitorId(int id){
        fieldVisitorId = id;
    }
    //endregion

    public String toString(){
        return FAnnotation.class.getSimpleName()+" descriptor="+descriptor+" visible="+visible;
    }
}
