package xyz.cofe.trambda.bc.cls;

import java.util.Arrays;
import xyz.cofe.trambda.bc.ByteCode;

public class CBegin implements ClsByteCode {
    private static final long serialVersionUID = 1;

    public CBegin(){}

    public CBegin(int version, int access, String name, String signature, String superName, String[] interfaces){
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    //region version
    protected int version;
    public int getVersion(){
        return version;
    }

    public void setVersion(int version){
        this.version = version;
    }
    //endregion
    //region access
    protected int access;
    public int getAccess(){
        return access;
    }

    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name : String
    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region signature : String
    protected String signature;
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region superName : String
    protected String superName;
    public String getSuperName(){
        return superName;
    }
    public void setSuperName(String superName){
        this.superName = superName;
    }
    //endregion
    //region interfaces : String[]
    protected String[] interfaces;
    public String[] getInterfaces(){
        return interfaces;
    }
    public void setInterfaces(String[] interfaces){
        this.interfaces = interfaces;
    }
    //endregion

    @Override
    public String toString(){
        return "ClassBegin " +
            "version=" + version +
            ", access=" + access +
            ", name='" + name + '\'' +
            ", signature='" + signature + '\'' +
            ", superName='" + superName + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
            "";
    }
}
