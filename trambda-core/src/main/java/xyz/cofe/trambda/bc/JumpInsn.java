package xyz.cofe.trambda.bc;

/**
 * the opcode of the type instruction to be visited. This opcode is either
 * IFEQ,
 * IFNE,
 * IFLT,
 * IFGE,
 * IFGT,
 * IFLE,
 * IF_ICMPEQ,
 * IF_ICMPNE,
 * IF_ICMPLT,
 * IF_ICMPGE,
 * IF_ICMPGT,
 * IF_ICMPLE,
 * IF_ACMPEQ,
 * IF_ACMPNE,
 *
 * GOTO,
 *
 * JSR,
 *
 * IFNULL or IFNONNULL.
 */
public class JumpInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public JumpInsn(){}
    public JumpInsn(int op,String label){
        this.opcode = op;
        this.label = label;
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
    //region label : String
    private String label;
    public String getLabel(){
        return label;
    }

    public void setLabel(String label){
        this.label = label;
    }
    //endregion

    public String toString(){
        return "JumpInsn "+
            OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+
            " "+label;
    }
}
