package xyz.cofe.trambda.bc;

public class VarInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public VarInsn(){}
    public VarInsn(int op, int vi){
        opcode = op;
        variable = vi;
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
    //region variable
    private int variable;
    public int getVariable(){
        return variable;
    }
    public void setVariable(int variable){
        this.variable = variable;
    }
    //endregion

    public String toString(){
        return "VarInsn "+OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+" "+variable;
    }
}
