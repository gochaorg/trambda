package xyz.cofe.trambda.bc;

import org.objectweb.asm.Label;

public class LocalVariable implements ByteCode {
    private static final long serialVersionUID = 1;

    public LocalVariable(){}

    public LocalVariable(String name,String descriptor,String signature,String labelStart,String labelEnd,int index){
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.labelStart = labelStart;
        this.labelEnd = labelEnd;
        this.index = index;
    }

    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    private String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }

    private String signature;
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }

    private String labelStart;
    public String getLabelStart(){
        return labelStart;
    }
    public void setLabelStart(String labelStart){
        this.labelStart = labelStart;
    }

    private String labelEnd;
    public String getLabelEnd(){
        return labelEnd;
    }
    public void setLabelEnd(String labelEnd){
        this.labelEnd = labelEnd;
    }

    private int index;
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
    }

    public String toString(){
        return "LocalVariable "+name+" "+descriptor+" "+signature+" "+labelStart+" "+labelEnd+" "+index;
    }
}
