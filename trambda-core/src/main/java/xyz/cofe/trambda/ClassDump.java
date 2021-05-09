package xyz.cofe.trambda;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CEnd;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CInnerClass;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.cls.CPermittedSubclass;
import xyz.cofe.trambda.bc.cls.CSource;
import xyz.cofe.trambda.bc.cls.CNestHost;
import xyz.cofe.trambda.bc.cls.CNestMember;
import xyz.cofe.trambda.bc.cls.COuterClass;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.fld.FieldByteCode;
import xyz.cofe.trambda.bc.mth.MethodByteCode;

/**
 * Дамп байт кода
 *
 * <p>
 * order:
 * <ol>
 *     <li> visit
 *     <li> [ visitSource ]
 *     <li> [ visitModule ]
 *     <li> [ visitNestHost ]
 *     <li> [ visitPermittedSubclass ]
 *     <li> [ visitOuterClass ]
 *     <li> ( visitAnnotation | visitTypeAnnotation | visitAttribute )*
 *     <li> ( visitNestMember | visitInnerClass | visitRecordComponent | visitField | visitMethod )*
 *     <li> visitEnd
 * </ol>
 */
public class ClassDump extends ClassVisitor {
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
     * Constructs a new {@link ClassVisitor}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one of {@link
     *            Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     */
    public ClassDump(int api){
        super(api);
    }

    public ClassDump(){
        super(Opcodes.ASM9);
    }

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public ClassDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    private void emit(ByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
    }

    protected final ThreadLocal<CBegin> currentClass = new ThreadLocal<>();
    protected Optional<CBegin> currentClass(){
        var v = currentClass.get();
        return v!=null ? Optional.of(v) : Optional.empty();
    }
    protected void currentClass(CBegin begin){
        currentClass.set(begin);
    }
    protected void currentClass(Consumer<CBegin> c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        var v = currentClass.get();
        if( v==null ){
            throw new IllegalThreadStateException("current class not defined");
        } else {
            c.accept(v);
        }
    }

    protected final ThreadLocal<Integer> currentIndex = new ThreadLocal<>();
    protected int currentIndex(){
        var i = currentIndex.get();
        if( i==null ){
            currentIndex.set(0);
            return 0;
        }
        return i;
    }
    protected int currentIndexGetAndInc(){
        var i = currentIndex.get();
        if( i==null ){
            currentIndex.set(1);
            return 0;
        }
        currentIndex.set(i+1);
        return i;
    }
    protected void currentIndex(int i){
        currentIndex.set(i);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
        var c = new CBegin(version,access,name,signature,superName,interfaces);
        currentClass(c);
        currentIndex(0);
        emit(c);
    }

    @Override
    public void visitSource(String source, String debug){
        int ci = currentIndexGetAndInc();

        var c = new CSource(source,debug);
        currentClass(x -> {
            x.setSource(c);
            x.getOrder().put(c,ci);
        });

        emit(c);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version){
        dump("module name="+name+" access="+(new AccFlags(access).flags())+" version="+version);
        return super.visitModule(name, access, version);
    }

    @Override
    public void visitNestHost(String nestHost){
        int ci = currentIndexGetAndInc();
        var c = new CNestHost(nestHost);

        currentClass( x -> {
            x.setNestHost(c);
            x.getOrder().put(c,ci);
        });

        emit(new CNestHost(nestHost));
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor){
        int ci = currentIndexGetAndInc();
        var c = new COuterClass(owner,name,descriptor);

        currentClass( x -> {
            x.setOuterClass(c);
            x.getOrder().put(c,ci);
        });

        emit(c);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
        int ci = currentIndexGetAndInc();
        CAnnotation a = new CAnnotation(descriptor,visible);

        AnnotationDump dump = new AnnotationDump(api);
        dump.byteCode(byteCodeConsumer,a);

        currentClass( x -> x.order(a,ci).getAnnotations().add(a) );

        emit(a);
        return dump;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        int ci = currentIndexGetAndInc();
        CTypeAnnotation a = new CTypeAnnotation(typeRef,typePath!=null ? typePath.toString():null,descriptor,visible);

        AnnotationDump dump = new AnnotationDump(api);
        dump.byteCode(byteCodeConsumer,a);

        currentClass( x -> x.order(a,ci).getTypeAnnotations().add(a) );

        emit(a);
        return dump;
    }

    @Override
    public void visitAttribute(Attribute attribute){
        dump("ann "+attribute);
    }

    @Override
    public void visitNestMember(String nestMember){
        int ci = currentIndexGetAndInc();
        var c = new CNestMember(nestMember);

        currentClass( x -> x.order(c,ci).getNestMembers().add(c) );

        emit(c);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass){
        int ci = currentIndexGetAndInc();
        var c = new CPermittedSubclass(permittedSubclass);

        currentClass( x -> x.order(c,ci).setPermittedSubclass(c) );

        emit(c);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access){
        int ci = currentIndexGetAndInc();
        var c = new CInnerClass(name,outerName,innerName,access);

        currentClass( x -> x.order(c,ci).getInnerClasses().add(c) );

        emit(c);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature){
        dump("recordComponent name="+name+" descriptor="+descriptor+" signature="+signature);
        return super.visitRecordComponent(name, descriptor, signature);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value){
        int ci = currentIndexGetAndInc();
        CField c = new CField(access,name,descriptor,signature,value);

        FieldDump dump = new FieldDump(api);
        dump.byteCode(b -> {
            if( byteCodeConsumer!=null )byteCodeConsumer.accept(b);
            if( b instanceof FieldByteCode ){
                c.getFieldByteCodes().add( (FieldByteCode) b);
            }
        });

        currentClass( x -> x.order(c,ci).getFields().add(c) );

        emit(c);
        return dump;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions){
        int ci = currentIndexGetAndInc();
        CMethod method = new CMethod(access,name,descriptor,signature,exceptions);

        MethodDump dump = new MethodDump(api);
        dump.byteCode(b -> {
            if( byteCodeConsumer!=null )byteCodeConsumer.accept(b);
            if( b instanceof MethodByteCode ){
                method.getMethodByteCodes().add((MethodByteCode) b);
            }
        });

        emit(method);

        currentClass( x -> x.order(method,ci).getMethods().add(method) );

        return dump;
    }

    @Override
    public void visitEnd(){
        emit(new CEnd());
    }
}
