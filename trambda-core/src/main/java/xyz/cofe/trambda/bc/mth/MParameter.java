package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;

public class MParameter extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MParameter(){}
    public MParameter(String name, int access){
        this.access = access;
        this.name = name;
    }
    public MParameter(MParameter sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        access = sample.access;
        name = sample.name;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MParameter clone(){ return new MParameter(this); }

    //region access : int
    private int access;

    public int getAccess(){
        return access;
    }

    public void setAccess(int acc){
        this.access = acc;
    }
    //endregion
    //region name : String
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion

    public String toString(){
        return MParameter.class.getSimpleName()+
            " name="+name+
            " access="+access+"#"+new AccFlags(access).flags()
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitParameter(getName(),getAccess());
    }
}
