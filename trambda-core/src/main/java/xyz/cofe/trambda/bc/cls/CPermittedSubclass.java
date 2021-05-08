package xyz.cofe.trambda.bc.cls;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.ByteCode;

public class CPermittedSubclass implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    public CPermittedSubclass(){}
    public CPermittedSubclass(String permittedSubclass){
        this.permittedSubclass = permittedSubclass;
    }

    //region permittedSubclass : String
    protected String permittedSubclass;

    public String getPermittedSubclass(){
        return permittedSubclass;
    }

    public void setPermittedSubclass(String permittedSubclass){
        this.permittedSubclass = permittedSubclass;
    }
    //endregion

    @Override
    public String toString(){
        return CPermittedSubclass.class.getSimpleName()+" " +
            "permittedSubclass=" + permittedSubclass;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitPermittedSubclass(getPermittedSubclass());
    }
}
