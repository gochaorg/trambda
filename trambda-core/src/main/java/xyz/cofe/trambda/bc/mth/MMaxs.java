package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

public class MMaxs extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MMaxs(){
    }

    public MMaxs(int maxStack, int maxLocals){
        this.maxLocals = maxLocals;
        this.maxStack = maxStack;
    }

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
        return "Maxs { stack="+maxStack+", locals="+maxLocals+" }";
    }
}
