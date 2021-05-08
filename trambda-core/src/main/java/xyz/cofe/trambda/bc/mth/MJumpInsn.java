package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

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
public class MJumpInsn extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MJumpInsn(){}
    public MJumpInsn(int op, String label){
        this.opcode = op;
        this.label = label;
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
        return MJumpInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+
            " label="+label;
    }
}
