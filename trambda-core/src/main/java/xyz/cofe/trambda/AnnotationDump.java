package xyz.cofe.trambda;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AnnotationDump extends AnnotationVisitor {
    /**
     * Constructs a new {@link AnnotationVisitor}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one of {@link
     *            Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     */
    public AnnotationDump(int api){
        super(api);
    }

    /**
     * Constructs a new {@link AnnotationVisitor}.
     *
     * @param api               the ASM API version implemented by this visitor. Must be one of {@link
     *                          Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param annotationVisitor the annotation visitor to which this visitor must delegate method
     *                          calls. May be {@literal null}.
     */
    public AnnotationDump(int api, AnnotationVisitor annotationVisitor){
        super(api, annotationVisitor);
    }

    /**
     * Visits a primitive value of the annotation.
     *
     * @param name  the value name.
     * @param value the actual value, whose type must be {@link Byte}, {@link Boolean}, {@link
     *              Character}, {@link Short}, {@link Integer} , {@link Long}, {@link Float}, {@link Double},
     *              {@link String} or {@link Type} of {@link Type#OBJECT} or {@link Type#ARRAY} sort. This
     *              value can also be an array of byte, boolean, short, char, int, long, float or double values
     *              (this is equivalent to using {@link #visitArray} and visiting each array element in turn,
     */
    @Override
    public void visit(String name, Object value){
        super.visit(name, value);
    }

    /**
     * Visits an enumeration value of the annotation.
     *
     * @param name       the value name.
     * @param descriptor the class descriptor of the enumeration class.
     * @param value      the actual enumeration value.
     */
    @Override
    public void visitEnum(String name, String descriptor, String value){
        super.visitEnum(name, descriptor, value);
    }

    /**
     * Visits a nested annotation value of the annotation.
     *
     * @param name       the value name.
     * @param descriptor the class descriptor of the nested annotation class.
     * @return a visitor to visit the actual nested annotation value, or {@literal null} if this
     * visitor is not interested in visiting this nested annotation. <i>The nested annotation
     * value must be fully visited before calling other methods on this annotation visitor</i>.
     */
    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor){
        return super.visitAnnotation(name, descriptor);
    }

    /**
     * Visits an array value of the annotation. Note that arrays of primitive values (such as byte,
     * boolean, short, char, int, long, float or double) can be passed as value to {@link #visit
     * visit}. This is what {@link ClassReader} does for non empty arrays of primitive values.
     *
     * @param name the value name.
     * @return a visitor to visit the actual array value elements, or {@literal null} if this visitor
     * is not interested in visiting these values. The 'name' parameters passed to the methods of
     * this visitor are ignored. <i>All the array values must be visited before calling other
     * methods on this annotation visitor</i>.
     */
    @Override
    public AnnotationVisitor visitArray(String name){
        return super.visitArray(name);
    }

    /**
     * Visits the end of the annotation.
     */
    @Override
    public void visitEnd(){
        super.visitEnd();
    }
}
