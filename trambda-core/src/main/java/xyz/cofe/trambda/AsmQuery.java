package xyz.cofe.trambda;

import java.io.IOError;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.Handle;
import xyz.cofe.trambda.bc.HandleArg;
import xyz.cofe.trambda.bc.InvokeDynamicInsn;
import xyz.cofe.trambda.bc.MethodDef;

public class AsmQuery<ENV> implements Query<ENV> {
    private static final Logger log = LoggerFactory.getLogger(AsmQuery.class);
    private static void log(String message,Object ... args){
        if( message==null )return;
        if( args==null || args.length==0 ){
            log.info(message);
        }else {
            log.info(message,args);
        }
    }

    protected Map<Fn<ENV, ? extends Object>, SerializedLambda> serializedLambdas = new LinkedHashMap<>();
    protected Map<Fn<ENV, ? extends Object>, MethodDef> fn2mdef = new LinkedHashMap<>();
    protected Map<String, MethodDef> methods = new LinkedHashMap<>();

    @Override
    public <RES> RES apply(Fn<ENV, RES> fn){
        if( fn==null )throw new IllegalArgumentException( "fn==null" );

        var sl = serializedLambdas.get(fn);
        var mdef = fn2mdef.get(fn);
        if( sl!=null && mdef!=null ){
            return call(fn, sl, mdef);
        }

        if( sl==null ){
            SerializedLambda sl0 = null;
            try{
                sl0 = getSerializedLambda(fn);
            } catch( Exception e ) {
                throw new IllegalArgumentException("can't fetch SerializedLambda of fn");
            }
            serializedLambdas.put(fn, sl0);
            sl = sl0;
        }

        mdef = getMethodDef(sl, fn);
        if( mdef==null ){
            throw new IllegalStateException("MethodDef not found");
        }
        fn2mdef.put(fn,mdef);

        return call( fn, sl, mdef);
    }
    protected <RES> RES call( Fn<ENV, RES> fn, SerializedLambda sl, MethodDef mdef ){
        return null;
    }

    protected String idOf( SerializedLambda sl ){
        return sl.getImplClass()+"/"+sl.getImplMethodName()+"/"+sl.getImplMethodSignature();
    }

    private ClassReader classReaderOf(Class baseResource,String className){
        var implClassUrl =
            baseResource.getResource("/"+className.replace(".","/")+".class");

        if( implClassUrl==null )throw new RuntimeException("bytecode of "+className+" not found");

        byte[] classByteCode = null;
        try{
            classByteCode = IOFun.readBytes(implClassUrl);
        } catch( IOException e ) {
            throw new IOError(e);
        }

        ClassReader cr = new ClassReader(classByteCode);
        return cr;
    }
    private Map<Handle,MethodDef> refs = new LinkedHashMap<>();
    private <RES> MethodDef getMethodDef( SerializedLambda sl, Fn<ENV, RES> fn ){
        var slId = idOf(sl);
        var cached = methods.get(slId);
        if( cached!=null ){
            return cached;
        }

        synchronized( this ){
            refs.clear();

            log("serialized lambda fetched");
            log("impl kind {}", methKind(sl));
            log("impl class {}", sl.getImplClass());
            log("impl meth name {}", sl.getImplMethodName());
            log("impl meth sign {}", sl.getImplMethodSignature());

            var mdef = getMethodDef(fn.getClass(), sl.getImplClass(), sl.getImplMethodName(), sl.getImplMethodSignature());
            if( mdef != null ){
                methods.put(slId, mdef);
            }

            return mdef;
        }
    }
    private synchronized <RES> MethodDef getMethodDef( Class baseClass, String implClass, String methName, String methSign ){
        ClassReader cr = classReaderOf(baseClass,implClass);

        List<ByteCode> byteCodes = new ArrayList<>();
        var mdef0 = new AtomicReference<MethodDef>();

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if( methName.equals(name) && descriptor!=null && descriptor.equals(methSign) ){
                    mdef0.set(new MethodDef(access,name,descriptor,signature,exceptions));
                    return dump(byteCodes::add);
                }
                return null;
            }
        };

        cr.accept(cv, 0);

        log.debug("byteCodes");
        byteCodes.forEach(bc -> log.debug(bc.toString()));

        if( mdef0.get()!=null ){
            mdef0.get().setByteCodes(byteCodes);

            var mdef = mdef0.get();
            byteCodes.stream()
                .map( bc -> bc instanceof InvokeDynamicInsn ? (InvokeDynamicInsn)bc : null )
                .filter(Objects::nonNull)
                .flatMap(idi ->
                    idi.getBootstrapMethodArguments().stream().map( arg -> {
                        if( !(arg instanceof HandleArg) ){
                            return null;
                        }
                        var ha = (HandleArg)arg;
                        var h = ha.getHandle();
                        if( h==null )return null;
                        if( h.getName().startsWith("lambda$") && h.getTag()==Opcodes.H_INVOKESTATIC ){
                            return h;
                        }
                        return null;
                    })
                )
                .filter( Objects::nonNull )
                .map( h -> {
                    log("found ref {}",h);
                    var rrefs = refs.get(h);
                    if( rrefs!=null ){
                        return rrefs;
                    }
                    var mdefChild = getMethodDef(baseClass, h.getOwner(), h.getName(), h.getDesc());
                    if( mdefChild!=null ){
                        refs.put(h,mdefChild);
                    }
                    return mdefChild;
                }).forEach( ref -> { mdef.getRefs().add(ref); });
        }

        return mdef0.get();
    }

    private SerializedLambda getSerializedLambda(Serializable lambda) throws Exception {
        final Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(lambda);
    }
    private static enum MethKind {
        GetField,
        PutField,
        GetStatic,
        PutStatic,
        InvokeVirtual,
        InvokeStatic,
        InvokeSpecial,
        NewInvokeSpecial,
        InvokeInterface
    }
    private static Optional<MethKind> methKind(SerializedLambda sl){
        switch( sl.getImplMethodKind() ){
            case MethodHandleInfo.REF_getField: return Optional.of(MethKind.GetField);
            case MethodHandleInfo.REF_getStatic: return Optional.of(MethKind.PutField);
            case MethodHandleInfo.REF_putField: return Optional.of(MethKind.GetStatic);
            case MethodHandleInfo.REF_putStatic: return Optional.of(MethKind.PutStatic);
            case MethodHandleInfo.REF_invokeVirtual: return Optional.of(MethKind.InvokeVirtual);
            case MethodHandleInfo.REF_invokeStatic: return Optional.of(MethKind.InvokeStatic);
            case MethodHandleInfo.REF_invokeSpecial: return Optional.of(MethKind.InvokeSpecial);
            case MethodHandleInfo.REF_newInvokeSpecial: return Optional.of(MethKind.NewInvokeSpecial);
            case MethodHandleInfo.REF_invokeInterface: return Optional.of(MethKind.InvokeInterface);
            default: return Optional.empty();
        }
    }
    private MethodVisitor dump(Consumer<ByteCode> consumer){
        return new MethodDump(Opcodes.ASM9).byteCode(consumer);
    }
}