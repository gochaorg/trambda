package xyz.cofe.trambda.bc.mth;

import java.util.Arrays;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class MLocalVariableAnnotation extends MAbstractBC implements ByteCode, AnnVisIdProperty, AnnotationDef {
    private static final long serialVersionUID = 1;

    public MLocalVariableAnnotation(){}

    //region annotationVisitorId : int
    protected int annotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){ return annotationVisitorId; };
    public void setAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    }
    //endregion
    //region typeRef : int
    protected int typeRef;
    public int getTypeRef(){
        return typeRef;
    }

    public void setTypeRef(int typeRef){
        this.typeRef = typeRef;
    }
    //endregion
    //region typePath : String
    protected String typePath;
    public String getTypePath(){
        return typePath;
    }

    public void setTypePath(String typePath){
        this.typePath = typePath;
    }
    //endregion
    //region startLabels : String[]
    protected String[] startLabels;
    public String[] getStartLabels(){
        return startLabels;
    }

    public void setStartLabels(String[] startLabels){
        this.startLabels = startLabels;
    }
    //endregion
    //region endLabels : String[]
    protected String[] endLabels;
    public String[] getEndLabels(){
        return endLabels;
    }

    public void setEndLabels(String[] endLabels){
        this.endLabels = endLabels;
    }
    //endregion
    //region index : int[]
    protected int[] index;
    public int[] getIndex(){
        return index;
    }

    public void setIndex(int[] index){
        this.index = index;
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
        return MLocalVariableAnnotation.class.getSimpleName()
            +" ann.v.id="+annotationVisitorId+
            " typeRef="+typeRef+
            " typePath="+typePath+
            " startLabels="+ Arrays.toString(startLabels) +
            " endLabels="+ Arrays.toString(endLabels) +
            " index="+ Arrays.toString(index) +
            " descriptor="+descriptor+
            " visible="+visible;
    }
}
