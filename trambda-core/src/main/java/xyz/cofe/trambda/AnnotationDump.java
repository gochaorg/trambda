package xyz.cofe.trambda;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AEnd;
import xyz.cofe.trambda.bc.ann.AEnum;
import xyz.cofe.trambda.bc.ann.APair;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

/**
 * Создание дампа байт-кода аннотаций
 */
public class AnnotationDump extends AnnotationVisitor {
    public static final AtomicInteger idSeq = new AtomicInteger(0);

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

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public AnnotationDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    public AnnotationDump byteCode(Consumer<? super ByteCode> bc, GetAnnotationByteCodes sencondConsumer){
        byteCodeConsumer = b -> {
            if( bc!=null ){
                bc.accept(b);
            }

            if( sencondConsumer!=null && b instanceof AnnotationByteCode ){
                sencondConsumer.getAnnotationByteCodes().add( (AnnotationByteCode)b );
            }
        };
        return this;
    }

    protected void emit(ByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
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
        emit(APair.create(name,value));
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
        emit(new AEnum(name,descriptor,value));
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
        EmANameDesc emOb = new EmANameDesc();

        AnnotationDump dump = new AnnotationDump(this.api);
        dump = dump.byteCode(byteCodeConsumer,emOb);

        emOb.setName(name);
        emOb.setDescriptor(descriptor);

        emit(emOb);

        return dump;
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
        EmAArray emOb = new EmAArray();

        AnnotationDump dump = new AnnotationDump(this.api);
        dump = dump.byteCode(byteCodeConsumer,emOb);

        emOb.setName(name);

        return dump;
    }

    /**
     * Visits the end of the annotation.
     */
    @Override
    public void visitEnd(){
        emit(new AEnd());
    }
}
