package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * end of the method
 */
public class MEnd extends MAbstractBC implements MethodWriter {
    public MEnd(){}
    public MEnd(MEnd sample){
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MEnd clone(){ return new MEnd(this); }

    private static final long serialVersionUID = 1;
    public String toString(){
        return MEnd.class.getSimpleName();
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
