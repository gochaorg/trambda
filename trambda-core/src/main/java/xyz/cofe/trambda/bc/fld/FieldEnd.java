package xyz.cofe.trambda.bc.fld;

import org.objectweb.asm.FieldVisitor;

/**
 * end of the method
 */
public class FieldEnd implements FieldByteCode {
    private static final long serialVersionUID = 1;

    public FieldEnd(){}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public FieldEnd(FieldEnd sample){}
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public FieldEnd clone(){
        return new FieldEnd(this);
    }

    public String toString(){
        return FieldEnd.class.getSimpleName();
    }

    @Override
    public void write(FieldVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
