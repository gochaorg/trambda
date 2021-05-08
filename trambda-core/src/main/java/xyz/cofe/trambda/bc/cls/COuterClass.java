package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.ByteCode;

public class COuterClass implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public COuterClass(){}

    public COuterClass(String owner, String name, String descriptor){
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }

    protected String owner;
    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    protected String name;
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    protected String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }

    @Override
    public String toString(){
        return COuterClass.class.getSimpleName()+" " +
            "owner='" + owner + '\'' +
            ", name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' ;
    }
}
