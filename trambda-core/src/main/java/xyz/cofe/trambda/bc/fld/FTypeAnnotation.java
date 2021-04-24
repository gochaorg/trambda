package xyz.cofe.trambda.bc.fld;

import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;

public class FTypeAnnotation implements FieldByteCode, AnnVisIdProperty {
    private static final long serialVersionUID = 1;

    public FTypeAnnotation(){
    }

    public FTypeAnnotation(int typeRef, String typePath, String descriptor, boolean visible){
        this.typeRef = typeRef;
        this.typePath = typePath;
        this.descriptor = descriptor;
        this.visible = visible;
    }

    protected int annotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){ return annotationVisitorId; };
    public void setAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    }

    protected int typeRef;
    public int getTypeRef(){
        return typeRef;
    }

    public void setTypeRef(int typeRef){
        this.typeRef = typeRef;
    }

    protected String typePath;
    public String getTypePath(){
        return typePath;
    }

    public void setTypePath(String typePath){
        this.typePath = typePath;
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

    protected int fieldVisitorId;

    @Override
    public int getFieldVisitorId(){
        return fieldVisitorId;
    }

    @Override
    public void setFieldVisitorId(int id){
        fieldVisitorId = id;
    }

    public String toString(){
        return FTypeAnnotation.class.getSimpleName()+" typeRef="+typeRef+" typePath="+typePath+" descriptor="+descriptor+" visible="+visible;
    }
}
