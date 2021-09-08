package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * Начала байт-кода метода
 */
public class MCode extends MAbstractBC implements MethodWriter {
    /**
     * Конструктор по умолчанию
     */
    public MCode(){
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MCode(MCode sample){
    }

    public MCode clone(){ return new MCode(this); }

    private static final long serialVersionUID = 1;
    public String toString(){
        return MCode.class.getSimpleName();
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitCode();
    }
}
