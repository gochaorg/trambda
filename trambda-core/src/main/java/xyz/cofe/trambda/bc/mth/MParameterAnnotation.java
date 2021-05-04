package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class MParameterAnnotation extends MAbstractBC implements ByteCode, AnnVisIdProperty, AnnotationDef {
    private static final long serialVersionUID = 1;

    //region annotationVisitorId : int
    protected int annotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){ return annotationVisitorId; };
    public void setAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    }
    //endregion
    //region parameter : int
    protected int parameter;
    public int getParameter(){
        return parameter;
    }
    public void setParameter(int parameter){
        this.parameter = parameter;
    }
    //endregion
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

    public String toString(){
        return MParameterAnnotation.class.getSimpleName()+
            " parameter="+parameter+" descriptor="+descriptor+" visible="+visible;
    }
}
