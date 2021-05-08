package xyz.cofe.trambda.bc.cls;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.ByteCode;

public class COuterClass implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    public COuterClass(){}

    public COuterClass(String owner, String name, String descriptor){
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }

    //region owner : String
    protected String owner;
    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
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

    @Override
    public String toString(){
        return COuterClass.class.getSimpleName()+" " +
            "owner=" + owner +
            " name=" + name +
            " descriptor=" + descriptor ;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitOuterClass(getOwner(),getName(),getDescriptor());
    }
}
