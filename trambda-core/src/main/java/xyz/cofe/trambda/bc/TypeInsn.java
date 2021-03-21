package xyz.cofe.trambda.bc;

public class TypeInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public TypeInsn(){}
    public TypeInsn(int op, String operand){
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
    private String operand;

    public String getOperand(){
        return operand;
    }

    public void setOperand(String operand){
        this.operand = operand;
    }
    //endregion

    public String toString(){
        return "TypeInsn "+OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+
            " "+operand
            ;
    }
}
