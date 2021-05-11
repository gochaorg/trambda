package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * Visits an instruction with a single int operand.
 *
 * <br>{@link #opcode}  the opcode of the instruction to be visited. This opcode is either BIPUSH, SIPUSH
 *                or NEWARRAY.
 * <br>{@link #operand} the operand of the instruction to be visited.<br>
 *                When opcode is BIPUSH, operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.
 *                <br>
 *                When opcode is SIPUSH, operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.
 *                <br>
 *                When opcode is NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link
 *                Opcodes#T_CHAR}, {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE},
 *                {@link Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
 */
public class MIntInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MIntInsn(){}
    public MIntInsn(int op, int operand){
        this.opcode = op;
        this.operand = operand;
    }
    public MIntInsn(MIntInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        opcode = sample.getOpcode();
        operand = sample.getOperand();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MIntInsn clone(){ return new MIntInsn(this); }

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
        return MIntInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+
            " operand="+operand
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitIntInsn(getOpcode(),getOperand());
    }
}
