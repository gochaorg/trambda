package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * the opcode of the instruction to be visited. This opcode is either
 *
 * NOP,
 * ACONST_NULL,
 *
 * ICONST_M1,
 *
 * ICONST_0,
 * ICONST_1,
 * ICONST_2,
 * ICONST_3,
 * ICONST_4,
 * ICONST_5,
 *
 * LCONST_0,
 * LCONST_1,
 *
 * FCONST_0,
 * FCONST_1,
 * FCONST_2,
 *
 * DCONST_0,
 * DCONST_1,
 *
 * IALOAD,
 * LALOAD,
 *
 * FALOAD,
 * DALOAD,
 * AALOAD,
 * BALOAD,
 * CALOAD,
 * SALOAD,
 *
 * IASTORE,
 * LASTORE,
 * FASTORE,
 * DASTORE,
 * AASTORE,
 * BASTORE,
 *
 * CASTORE,
 * SASTORE,
 *
 * POP,
 * POP2,
 *
 * DUP,
 * DUP_X1,
 * DUP_X2,
 * DUP2,
 * DUP2_X1,
 * DUP2_X2,
 *
 * SWAP,
 *
 * IADD,
 * LADD,
 * FADD,
 * DADD,
 *
 * ISUB,
 * LSUB,
 * FSUB,
 * DSUB,
 *
 * IMUL,
 * LMUL,
 * FMUL,
 * DMUL,
 *
 * IDIV,
 * LDIV,
 * FDIV,
 * DDIV,
 *
 * IREM,
 * LREM,
 * FREM,
 * DREM,
 *
 * INEG,
 * LNEG,
 * FNEG,
 * DNEG,
 *
 * ISHL,
 * LSHL,
 *
 * ISHR,
 * LSHR,
 * IUSHR,
 * LUSHR,
 *
 * IAND,
 * LAND,
 *
 * IOR,
 * LOR,
 * IXOR,
 * LXOR,
 *
 * I2L,
 * I2F,
 * I2D,
 * L2I,
 * L2F,
 * L2D,
 * F2I,
 * F2L,
 * F2D,
 * D2I,
 * D2L,
 * D2F,
 * I2B,
 * I2C,
 * I2S,
 *
 * LCMP,
 * FCMPL,
 * FCMPG,
 * DCMPL,
 * DCMPG,
 *
 * IRETURN,
 * LRETURN,
 * FRETURN,
 * DRETURN,
 * ARETURN,
 * RETURN,
 *
 * ARRAYLENGTH,
 *
 * ATHROW,
 *
 * MONITORENTER, or MONITOREXIT.
 */
public class MInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MInsn(){}
    public MInsn(int op){this.opcode = op;}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MInsn(MInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.opcode = sample.getOpcode();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MInsn clone(){ return new MInsn(this); }

    //region opcode
    private int opcode;

    public int getOpcode(){
        return opcode;
    }

    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion

    public String toString(){
        return MInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitInsn(getOpcode());
    }
}
