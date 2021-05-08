package xyz.cofe.trambda.bc.cls;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;

public class CInnerClass implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    public CInnerClass(){}

    public CInnerClass(String name, String outerName, String innerName, int access){
        this.name = name;
        this.outerName = outerName;
        this.innerName = innerName;
        this.access = access;
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
    //region outerName : String
    protected String outerName;
    public String getOuterName(){
        return outerName;
    }
    public void setOuterName(String outerName){
        this.outerName = outerName;
    }
    //endregion
    //region innerName : String
    protected String innerName;
    public String getInnerName(){
        return innerName;
    }
    public void setInnerName(String innerName){
        this.innerName = innerName;
    }
    //endregion
    //region access : Int
    protected int access;
    public int getAccess(){
        return access;
    }
    public void setAccess(int access){
        this.access = access;
    }
    //endregion

    @Override
    public String toString(){
        return CInnerClass.class.getSimpleName() +
            " name=" + name +
            " outerName=" + outerName +
            " innerName=" + innerName +
            " access=" + access+("#"+new AccFlags(access).flags()) ;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitInnerClass(getName(),getOuterName(),getInnerName(),getAccess());
    }
}
