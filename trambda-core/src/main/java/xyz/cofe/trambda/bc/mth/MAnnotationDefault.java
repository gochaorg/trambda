package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class MAnnotationDefault extends MAbstractBC implements ByteCode, AnnVisIdProperty, AnnotationDef {
    private static final long serialVersionUID = 1;

    public MAnnotationDefault(){}
    public MAnnotationDefault(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }

    //region annotationVisitorId : int
    private int annotationVisitorId = DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){
        return annotationVisitorId;
    }
    public void setAnnotationVisitorId(int annotationVisitorId){
        this.annotationVisitorId = annotationVisitorId;
    }
    //endregion

    public String toString(){
        return MAnnotationDefault.class.getSimpleName()+" visitorId="+annotationVisitorId;
    }
}
