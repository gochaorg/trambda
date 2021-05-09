package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

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
