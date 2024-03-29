package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

public class MAnnotableParameterCount extends MAbstractBC implements ByteCode, MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MAnnotableParameterCount(){}
    public MAnnotableParameterCount(int parameterCount, boolean visible){
        this.parameterCount = parameterCount;
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MAnnotableParameterCount(MAnnotableParameterCount sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        parameterCount = sample.getParameterCount();
        visible = sample.isVisible();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MAnnotableParameterCount clone(){ return new MAnnotableParameterCount(this); }

    //region parameterCount : int
    protected int parameterCount;
    public int getParameterCount(){
        return parameterCount;
    }
    public void setParameterCount(int parameterCount){
        this.parameterCount = parameterCount;
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
        return MAnnotableParameterCount.class.getSimpleName()+" parameterCount="+parameterCount+" visible="+visible;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitAnnotableParameterCount(
            getParameterCount(),
            isVisible()
        );
    }
}
