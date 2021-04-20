package xyz.cofe.trambda.bc;

import org.objectweb.asm.TypePath;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;

public class TypeAnnotation implements ByteCode, AnnVisIdProperty {
    private static final long serialVersionUID = 1;

    public TypeAnnotation(){}
    public TypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        this.typeRef = typeRef;
        this.typePath = typePath!=null ? typePath.toString() : null;
        this.descriptor = descriptor;
        this.visible = visible;
    }

    //region annotationVisitorId : int
    protected int annotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){ return annotationVisitorId; };
    public void setAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    }
    //endregion
    //region typeRef
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
        return TypeAnnotation.class.getSimpleName()+
            " ann.v.id="+annotationVisitorId+
            " typeRef="+typeRef+
            " typePath="+typePath+
            " descriptor="+descriptor+
            " visible="+visible;
    }
}
