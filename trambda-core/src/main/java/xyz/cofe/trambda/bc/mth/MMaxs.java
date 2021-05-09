package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

public class MMaxs extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MMaxs(){
    }

    public MMaxs(int maxStack, int maxLocals){
        this.maxLocals = maxLocals;
        this.maxStack = maxStack;
    }

    public MMaxs(MMaxs sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        maxLocals = sample.maxLocals;
        maxStack = sample.maxStack;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MMaxs clone(){ return new MMaxs(this); }

    //region maxStack : int
    private int maxStack;
    public int getMaxStack(){
        return maxStack;
    }
    public void setMaxStack(int maxStack){
        this.maxStack = maxStack;
    }
    //endregion
    //region maxLocals : int
    private int maxLocals;
    public int getMaxLocals(){
        return maxLocals;
    }
    public void setMaxLocals(int maxLocals){
        this.maxLocals = maxLocals;
    }
    //endregion

    public String toString(){
        return MMaxs.class.getSimpleName()+
            " stack="+maxStack+
            " locals="+maxLocals+"";
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitMaxs(getMaxStack(),getMaxLocals());
    }
}
