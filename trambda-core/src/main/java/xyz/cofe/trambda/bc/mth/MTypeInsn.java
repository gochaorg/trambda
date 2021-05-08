package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * the opcode of the type instruction to be visited. This opcode is either NEW,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.anewarray">ANEWARRAY</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.checkcast">CHECKCAST</a> or
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.instanceof">INSTANCEOF</a>.
 */
public class MTypeInsn extends MAbstractBC implements ByteCode, MethodWriter {
    private static final long serialVersionUID = 1;

    public MTypeInsn(){}
    public MTypeInsn(int op, String type){
        this.opcode = op;
        this.type = type;
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
    //region type : String
    private String type;

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
    //endregion

    public String toString(){
        return MTypeInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+
            " operand="+ type
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitTypeInsn(getOpcode(), getType());
    }
}
