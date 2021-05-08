package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

/**
 * the opcode of the type instruction to be visited. This opcode is either
 * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
 */
public class MMethodInsn extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MMethodInsn(){}
    public MMethodInsn(int op, String owner, String name, String descriptor, boolean iface){
        this.opcode = op;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.iface = iface;
    }

    //region opcode : int
    private int opcode;
    public int getOpcode(){
        return opcode;
    }
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region owner : String
    private String owner;
    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
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
    //region iface : boolean
    private boolean iface;

    public boolean isIface(){
        return iface;
    }

    public void setIface(boolean iface){
        this.iface = iface;
    }
    //endregion

    public String toString(){
        return MMethodInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+"" +
            " owner="+owner+" name="+name+" desc="+descriptor+" iface="+iface
            ;
    }
}
