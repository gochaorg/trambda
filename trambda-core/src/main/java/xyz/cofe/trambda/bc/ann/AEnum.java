package xyz.cofe.trambda.bc.ann;

import org.objectweb.asm.AnnotationVisitor;

public class AEnum extends AAbstractBC implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    public AEnum(){}
    public AEnum(String name, String descriptor, String value){
        this.name = name;
        this.descriptor = descriptor;
        this.value = value;
    }

    //region name : String
    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
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
    //region value : String
    protected String value;

    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
    //endregion

    public String toString(){
        return AEnum.class.getSimpleName()+" name="+name+
            " descriptor="+descriptor+
            " value="+(value != null ? "\""+value+"\"" : "null" );
    }

    @Override
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnum(getName(),getDescriptor(),getValue());
    }
}
