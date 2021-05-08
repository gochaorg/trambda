package xyz.cofe.trambda.bc.ann;

import org.objectweb.asm.AnnotationVisitor;

public interface AnnotationWriter {
    void write(AnnotationVisitor v);
}
