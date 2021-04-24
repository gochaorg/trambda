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

    //region opcode
    private int opcode;
    public int getOpcode(){
        return opcode;
    }
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion

    private String owner;
    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
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

    private boolean iface;

    public boolean isIface(){
        return iface;
    }

    public void setIface(boolean iface){
        this.iface = iface;
    }

    public String toString(){
        return "MethodInsn "+
            OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+"" +
            " owner="+owner+" name="+name+" desc="+descriptor+" iface="+iface
            ;
    }
}
