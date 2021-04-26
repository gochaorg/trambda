package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class MAnnotableParameterCount extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MAnnotableParameterCount(){}
    public MAnnotableParameterCount(int parameterCount, boolean visible){
        this.parameterCount = parameterCount;
        this.visible = visible;
    }

    protected int parameterCount;
    public int getParameterCount(){
        return parameterCount;
    }

    public void setParameterCount(int parameterCount){
        this.parameterCount = parameterCount;
    }

    protected boolean visible;

    public boolean isVisible(){
        return visible;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public String toString(){
        return MAnnotableParameterCount.class.getSimpleName()+" parameterCount="+parameterCount+" visible="+visible;
    }
}
