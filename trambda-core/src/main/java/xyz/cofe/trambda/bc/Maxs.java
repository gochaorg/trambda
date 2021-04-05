package xyz.cofe.trambda.bc;

public class Maxs implements ByteCode {
    private static final long serialVersionUID = 1;

    public Maxs(){
    }

    public Maxs(int maxStack,int maxLocals){
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
