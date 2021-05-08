package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.Label;
import xyz.cofe.trambda.bc.ByteCode;

public class MLocalVariable extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MLocalVariable(){}

    public MLocalVariable(String name, String descriptor, String signature, String labelStart, String labelEnd, int index){
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.labelStart = labelStart;
        this.labelEnd = labelEnd;
        this.index = index;
    }

    //region name : String
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String
    private String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region signature : String
    private String signature;
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region labelStart : String
    private String labelStart;
    public String getLabelStart(){
        return labelStart;
    }
    public void setLabelStart(String labelStart){
        this.labelStart = labelStart;
    }
    //endregion
    //region labelEnd : String
    private String labelEnd;
    public String getLabelEnd(){
        return labelEnd;
    }
    public void setLabelEnd(String labelEnd){
        this.labelEnd = labelEnd;
    }
    //endregion
    //region index : int
    private int index;
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
    }
    //endregion

    public String toString(){
        return MLocalVariable.class.getSimpleName()+
            " name="+name+
            " descriptor="+descriptor+
            " signature="+signature+
            " start="+labelStart+
            " end="+labelEnd+
            " index="+index;
    }
}
