package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

public class MIntInsn extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MIntInsn(){}
    public MIntInsn(int op, int operand){
        this.opcode = op;
        this.operand = operand;
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
    //region operand : int
    private int operand;

    public int getOperand(){
        return operand;
    }

    public void setOperand(int operand){
        this.operand = operand;
    }
    //endregion

    public String toString(){
        return MIntInsn.class.getSimpleName()+" "+ OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+
            " "+operand
            ;
    }
}
