package xyz.cofe.trambda.bc;

public class IntInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public IntInsn(){}
    public IntInsn(int op, int operand){
        this.opcode = op;
        this.operand = operand;
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
    //region operand
    private int operand;

    public int getOperand(){
        return operand;
    }

    public void setOperand(int operand){
        this.operand = operand;
    }
    //endregion

    public String toString(){
        return "IntInsn "+OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+
            " "+operand
            ;
    }
}
