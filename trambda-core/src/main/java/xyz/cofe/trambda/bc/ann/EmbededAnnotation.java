package xyz.cofe.trambda.bc.ann;

import java.util.ArrayList;
import java.util.List;

public abstract class EmbededAnnotation extends AAbstractBC implements AnnotationDef, GetAnnotationByteCodes {
    //region embededAnnotationVisitorId : int
//    protected int embededAnnotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
//    public int getEmbededAnnotationVisitorId(){ return annotationVisitorId; };
//    public void setEmbededAnnotationVisitorId(int id){
//        this.annotationVisitorId = id;
//    };
    //endregion

//    @Override
//    public int getAnnotationDefVisitorId(){ return getEmbededAnnotationVisitorId(); }

    //region annotationByteCodes : List<AnnotationByteCode>
    protected List<AnnotationByteCode> annotationByteCodes;
    public List<AnnotationByteCode> getAnnotationByteCodes(){
        if(annotationByteCodes==null)annotationByteCodes = new ArrayList<>();
        return annotationByteCodes;
    }
    public void setAnnotationByteCodes(List<AnnotationByteCode> byteCodes){
        annotationByteCodes = byteCodes;
    }
    //endregion
}
