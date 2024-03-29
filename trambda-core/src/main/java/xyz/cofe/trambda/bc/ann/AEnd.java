package xyz.cofe.trambda.bc.ann;

import org.objectweb.asm.AnnotationVisitor;

public class AEnd extends AAbstractBC implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public AEnd(){}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public AEnd(AEnd sample){
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public AEnd clone(){
        return new AEnd(this);
    }

    public String toString(){
        return AEnd.class.getSimpleName();
    }

    @Override
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
