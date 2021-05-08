package xyz.cofe.trambda.bc.fld;

import org.objectweb.asm.FieldVisitor;

public interface FieldWriter {
    void write(FieldVisitor v);
}
