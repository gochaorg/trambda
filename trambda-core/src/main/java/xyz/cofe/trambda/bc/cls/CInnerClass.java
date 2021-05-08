package xyz.cofe.trambda.bc.cls;

import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;

public class CInnerClass implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public CInnerClass(){}

    public CInnerClass(String name, String outerName, String innerName, int access){
        this.name = name;
        this.outerName = outerName;
        this.innerName = innerName;
        this.access = access;
    }

    protected String name;
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    protected String outerName;
    public String getOuterName(){
        return outerName;
    }

    public void setOuterName(String outerName){
        this.outerName = outerName;
    }

    protected String innerName;
    public String getInnerName(){
        return innerName;
    }

    public void setInnerName(String innerName){
        this.innerName = innerName;
    }

    protected int access;

    public int getAccess(){
        return access;
    }

    public void setAccess(int access){
        this.access = access;
    }

    @Override
    public String toString(){
        return CInnerClass.class.getSimpleName() +
            " name=" + name +
            " outerName=" + outerName +
            " innerName=" + innerName +
            " access=" + access+("#"+new AccFlags(access).flags()) ;
    }
}
