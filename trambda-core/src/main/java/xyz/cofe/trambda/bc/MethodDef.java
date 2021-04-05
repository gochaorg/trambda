package xyz.cofe.trambda.bc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MethodDef implements Serializable {
    private static final long serialVersionUID = 1;

    public MethodDef(){
    }

    public MethodDef(int access, String name,String descriptor,String signature,String[] exceptions){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    //region byteCodes
    private List<ByteCode> byteCodes;
    public List<ByteCode> getByteCodes(){
        if( byteCodes==null )byteCodes = new ArrayList<>();
        return byteCodes;
    }
    public void setByteCodes(List<ByteCode> byteCodes){
        this.byteCodes = byteCodes;
    }
    //endregion
    //region access
    private int access;
    public int getAccess(){
        return access;
    }
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region flags
    public AccFlags getFlags(){
        return new AccFlags(access);
    }
    public void setFlags(AccFlags flags){
        if( flags==null )throw new IllegalArgumentException( "flags==null" );
        this.access = flags.value();
    }
    //endregion
    //region name
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor
    private String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region signature
    private String signature;
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region exceptions
    private String[] exceptions;
    public String[] getExceptions(){
        return exceptions;
    }
    public void setExceptions(String[] exceptions){
        this.exceptions = exceptions;
    }
    //endregion

    //region refs
    private List<MethodDef> refs;
    public synchronized List<MethodDef> getRefs(){
        if( refs==null ){
            refs = new ArrayList<>();
        }
        return refs;
    }
    public synchronized void setRefs(List<MethodDef> refs){ this.refs = refs; }
    //endregion

    public String[] getParamTypes(){
        String desc = getDescriptor();
        return null;
    }
}
