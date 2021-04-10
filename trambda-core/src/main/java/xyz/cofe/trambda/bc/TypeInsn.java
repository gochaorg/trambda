package xyz.cofe.trambda.bc;

/**
 * the opcode of the type instruction to be visited. This opcode is either NEW,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.anewarray">ANEWARRAY</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.checkcast">CHECKCAST</a> or
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.instanceof">INSTANCEOF</a>.
 */
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
