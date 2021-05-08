package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

public class MAnnotationDefault extends MAbstractBC implements ByteCode, AnnVisIdProperty, AnnotationDef, GetAnnotationByteCodes {
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

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public Eterable<ByteCode> nodes(){
        if( annotationByteCodes!=null )return Eterable.of(annotationByteCodes);
        return Eterable.empty();
    }
}
