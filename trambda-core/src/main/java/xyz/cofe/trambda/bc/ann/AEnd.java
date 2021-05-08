package xyz.cofe.trambda.bc.ann;

import org.objectweb.asm.AnnotationVisitor;

public class AEnd extends AAbstractBC implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    public AEnd(){}

    public String toString(){
        return AEnd.class.getSimpleName();
    }

    @Override
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
