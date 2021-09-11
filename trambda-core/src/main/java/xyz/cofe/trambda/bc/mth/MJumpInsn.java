package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;

/**
 * Инструкция перехода
 * 
 * <p>
 * Возможные OpCode:
 * 
 * {@link OpCode#IFEQ},
 * {@link OpCode#IFNE},
 * {@link OpCode#IFLT},
 * {@link OpCode#IFGE},
 * {@link OpCode#IFGT},
 * {@link OpCode#IFLE},
 * {@link OpCode#IF_ICMPEQ},
 * {@link OpCode#IF_ICMPNE},
 * {@link OpCode#IF_ICMPLT},
 * {@link OpCode#IF_ICMPGE},
 * {@link OpCode#IF_ICMPGT},
 * {@link OpCode#IF_ICMPLE},
 * {@link OpCode#IF_ACMPEQ},
 * {@link OpCode#IF_ACMPNE},
 *
 * {@link OpCode#GOTO},
 *
 * {@link OpCode#JSR},
 *
 * {@link OpCode#IFNULL} or 
 * {@link OpCode#IFNONNULL}.
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
    
    /**
     * Возвращает код инстркуции
     * <p>
     * см
    * {@link OpCode#IFEQ},
    * {@link OpCode#IFNE},
    * {@link OpCode#IFLT},
    * {@link OpCode#IFGE},
    * {@link OpCode#IFGT},
    * {@link OpCode#IFLE},
    * {@link OpCode#IF_ICMPEQ},
    * {@link OpCode#IF_ICMPNE},
    * {@link OpCode#IF_ICMPLT},
    * {@link OpCode#IF_ICMPGE},
    * {@link OpCode#IF_ICMPGT},
    * {@link OpCode#IF_ICMPLE},
    * {@link OpCode#IF_ACMPEQ},
    * {@link OpCode#IF_ACMPNE},
    *
    * {@link OpCode#GOTO},
    *
    * {@link OpCode#JSR},
    *
    * {@link OpCode#IFNULL} or 
    * {@link OpCode#IFNONNULL}.
     * @return код инструкции
     */
    public int getOpcode(){
        return opcode;
    }
    
    /**
     * Указывает код инструкции
     * <p>
     * см
    * {@link OpCode#IFEQ},
    * {@link OpCode#IFNE},
    * {@link OpCode#IFLT},
    * {@link OpCode#IFGE},
    * {@link OpCode#IFGT},
    * {@link OpCode#IFLE},
    * {@link OpCode#IF_ICMPEQ},
    * {@link OpCode#IF_ICMPNE},
    * {@link OpCode#IF_ICMPLT},
    * {@link OpCode#IF_ICMPGE},
    * {@link OpCode#IF_ICMPGT},
    * {@link OpCode#IF_ICMPLE},
    * {@link OpCode#IF_ACMPEQ},
    * {@link OpCode#IF_ACMPNE},
    *
    * {@link OpCode#GOTO},
    *
    * {@link OpCode#JSR},
    *
    * {@link OpCode#IFNULL} or 
    * {@link OpCode#IFNONNULL}.
     * @param opcode код инструкции
     */
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region label : String
    private String label;
    
    /**
     * Возвращает метку перехода
     * @return метка перехода
     */
    public String getLabel(){
        return label;
    }

    /**
     * Указывает метку перехода
     * @param label метка перехода
     */
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
