package xyz.cofe.trambda.bc.cls;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public interface ClazzWriter {
    void write(ClassWriter v);
}
