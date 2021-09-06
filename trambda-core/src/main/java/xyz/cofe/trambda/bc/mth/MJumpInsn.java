package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
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
public class MJumpInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MJumpInsn(){}
    public MJumpInsn(int op, String label){
        this.opcode = op;
        this.label = label;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MJumpInsn(MJumpInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        opcode = sample.getOpcode();
        label = sample.getLabel();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MJumpInsn clone(){ return new MJumpInsn(this); }

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

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        v.visitJumpInsn(getOpcode(), ctx.labelCreateOrGet(getLabel()));
    }
}
