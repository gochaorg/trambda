package xyz.cofe.trambda.bc;

public class MultiANewArrayInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public MultiANewArrayInsn(){
    }
    public MultiANewArrayInsn(String descriptor, int numDimensions){
        this.descriptor = descriptor;
        this.numDimensions = numDimensions;
    }

    //region descriptor
    private String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region numDimensions
    private int numDimensions;

    public int getNumDimensions(){
        return numDimensions;
    }

    public void setNumDimensions(int numDimensions){
        this.numDimensions = numDimensions;
    }
    //endregion

    public String toString(){
        return "MultiANewArrayInsn descriptor="+descriptor+" numDimensions="+numDimensions;
    }
}
