package xyz.cofe.trambda;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.fld.FAnnotation;
import xyz.cofe.trambda.bc.fld.FieldEnd;
import xyz.cofe.trambda.bc.fld.FldByteCode;
import xyz.cofe.trambda.bc.fld.FTypeAnnotation;
import xyz.cofe.trambda.bc.fld.FldVisIdProperty;

public class FieldDump extends FieldVisitor {
    private void dump(String message,Object...args){
        if( message==null )return;
        if( args==null || args.length==0 ){
            System.out.println(message);
        }else{
            System.out.print(message);
            for( var a : args ){
                System.out.print(" ");
                System.out.print(a);
            }
            System.out.println();
        }
    }

    /**
     * Constructs a new {@link FieldVisitor}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one of {@link
     *            Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6}, {@link Opcodes#ASM7}, {@link
     *            Opcodes#ASM8} or {@link Opcodes#ASM9}.
     */
    public FieldDump(int api){
        super(api);
    }

    /**
     * Constructs a new {@link FieldVisitor}.
     *
     * @param api          the ASM API version implemented by this visitor. Must be one of {@link
     *                     Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6}, {@link Opcodes#ASM7} or {@link
     *                     Opcodes#ASM8}.
     * @param fieldVisitor the field visitor to which this visitor must delegate method calls. May be
     */
    public FieldDump(int api, FieldVisitor fieldVisitor){
        super(api, fieldVisitor);
    }

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public FieldDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    public static final AtomicInteger idSeq = new AtomicInteger(0);

    protected Integer fieldVisitorId;
    public Integer getFieldVisitorId(){ return fieldVisitorId; }
    public void setFieldVisitorId(Integer id){
        fieldVisitorId = id;
    }
    public FieldDump fieldVisitorId(Integer id){
        fieldVisitorId = id;
        return this;
    }

    {
        fieldVisitorId = idSeq.incrementAndGet();
    }

    protected void emit(FldByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        if( bc instanceof FldVisIdProperty ){
            ((FldVisIdProperty)bc).setFieldVisitorId(getFieldVisitorId());
        }
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
    }

    /**
     * Visits an annotation of the field.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
        AnnotationDump dump = new AnnotationDump(this.api);
        dump.byteCode( annVisitor.get() );

        FAnnotation a = new FAnnotation(descriptor,visible);
        a.setAnnotationVisitorId(dump.getAnnotationVisitorId());

        emit(a);
        return dump;
    }

    @SuppressWarnings("unchecked")
    private Supplier<Consumer<? super AnnVisIdProperty>> annVisitor = ()->{
        //noinspection rawtypes
        return (Consumer)this.byteCodeConsumer;
    };

    public FieldDump annotationByteCode( Consumer<? super AnnVisIdProperty> cons ){
        if( cons==null )throw new IllegalArgumentException( "cons==null" );
        annVisitor = () -> cons;
        return this;
    }

    /**
     * Visits an annotation on the type of the field.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link TypeReference#FIELD}. See {@link TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        AnnotationDump dump = new AnnotationDump(this.api);
        dump.byteCode( annVisitor.get() );

        FTypeAnnotation ta = new FTypeAnnotation();
        ta.setAnnotationVisitorId(dump.getAnnotationVisitorId());
        ta.setTypeRef(typeRef);
        ta.setTypePath(typePath!=null ? typePath.toString() : null);
        ta.setDescriptor(descriptor);
        ta.setVisible(visible);

        emit(ta);
        return dump;
    }

    /**
     * Visits a non standard attribute of the field.
     *
     * @param attribute an attribute.
     */
    @Override
    public void visitAttribute(Attribute attribute){
        dump("visitAttribute ",attribute);
        super.visitAttribute(attribute);
    }

    /**
     * Visits the end of the field. This method, which is the last one to be called, is used to inform
     * the visitor that all the annotations and attributes of the field have been visited.
     */
    @Override
    public void visitEnd(){
        emit(new FieldEnd());
    }
}
