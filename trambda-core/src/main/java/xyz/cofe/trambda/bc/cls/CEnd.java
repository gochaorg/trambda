package xyz.cofe.trambda.bc.cls;

import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.ByteCode;

public class CEnd implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    public CEnd(){}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CEnd(CEnd sample){
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CEnd clone(){ return new CEnd(this); }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CEnd configure(Consumer<CEnd> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    public String toString(){ return CEnd.class.getSimpleName(); }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
