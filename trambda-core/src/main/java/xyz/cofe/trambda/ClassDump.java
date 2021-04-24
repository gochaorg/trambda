package xyz.cofe.trambda;

import java.util.Arrays;
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

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
        emit(new CBegin(version,access,name,signature,superName,interfaces));
    }

    @Override
    public void visitSource(String source, String debug){
        emit(new CSource(source,debug));
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version){
        dump("module name="+name+" access="+(new AccFlags(access).flags())+" version="+version);
        return super.visitModule(name, access, version);
    }

    @Override
    public void visitNestHost(String nestHost){
        emit(new CNestHost(nestHost));
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor){
        emit(new COuterClass(owner,name,descriptor));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
        AnnotationDump dump = new AnnotationDump(api);
        dump.byteCode(byteCodeConsumer);

        CAnnotation a = new CAnnotation(descriptor,visible);
        a.setAnnotationVisitorId(dump.getAnnotationVisitorId());

        return dump;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        AnnotationDump dump = new AnnotationDump(api);
        dump.byteCode(byteCodeConsumer);

        CTypeAnnotation c = new CTypeAnnotation(typeRef,typePath!=null ? typePath.toString():null,descriptor,visible);
        c.setAnnotationVisitorId(dump.getAnnotationVisitorId());
        return dump;
    }

    @Override
    public void visitAttribute(Attribute attribute){
        dump("ann "+attribute);
    }

    @Override
    public void visitNestMember(String nestMember){
        emit(new CNestMember(nestMember));
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass){
        emit(new CPermittedSubclass(permittedSubclass));
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access){
        emit(new CInnerClass(name,outerName,innerName,access));
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature){
        dump("recordComponent name="+name+" descriptor="+descriptor+" signature="+signature);
        return super.visitRecordComponent(name, descriptor, signature);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value){
        FieldDump dump = new FieldDump(api);
        dump.byteCode(byteCodeConsumer);

        CField c = new CField(access,name,descriptor,signature,value);
        c.setFieldVisitorId(dump.getFieldVisitorId());

        emit(c);
        return dump;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions){
        MethodDump dump = new MethodDump(api);
        dump.byteCode(byteCodeConsumer);

        CMethod method = new CMethod(access,name,descriptor,signature,exceptions);
        method.setMethodVisitorId(dump.getMethodVisitorId());
        emit(method);

        return dump;
    }

    @Override
    public void visitEnd(){
        emit(new CEnd());
    }
}
