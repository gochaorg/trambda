package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.LongArg;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.mth.*;

public class Method implements GetAnnotations, GetDefinition {
    public Method( CMethod method, List<? extends ByteCode> byteCode ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( method==null )throw new IllegalArgumentException( "method==null" );

        this.definition = method;

        body = byteCode.stream()
            .map( b -> b instanceof MethodByteCode ? (MethodByteCode)b : null)
            .filter( Objects::nonNull )
            .filter( b -> b.getMethodVisitorId()==method.getMethodVisitorId() )
            .collect(Collectors.toUnmodifiableList());

        annotations = body.stream()
            .map( a -> a instanceof AnnotationDef ? (AnnotationDef)a : null )
            .filter( Objects::nonNull )
            .map( a -> new Annotation(a,byteCode).annotationDefVisitorId(a.getAnnotationDefVisitorId()) )
            .collect(Collectors.toUnmodifiableList());
    }

    //region definition : CMethod
    protected CMethod definition;
    public synchronized CMethod getDefinition(){
        return definition;
    }
    public synchronized void setDefinition(CMethod definition){
        this.definition = definition;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected List<MethodByteCode> body;
    public synchronized List<MethodByteCode> getBody(){
        if( body==null )body = new ArrayList<>();
        return body;
    }
    public synchronized void setBody(List<MethodByteCode> body){
        this.body = body;
    }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public synchronized List<Annotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }
    public synchronized void setAnnotations(List<Annotation> annotations){
        this.annotations = annotations;
    }
    //endregion

    protected transient Map<String, Label> labels;
    protected transient MethodVisitor mv;

    public synchronized void write(ClassWriter cw){
        if( cw==null )throw new IllegalArgumentException( "cw==null" );
        if( definition==null )throw new IllegalStateException("definition==null");
        if( body==null )throw new IllegalStateException("body==null");

        labels = new LinkedHashMap<>();
        targetHandles = new HashMap<>();
        mv  = cw.visitMethod(definition.getAccess(), definition.getName(), definition.getDescriptor(), definition.getSignature(), definition.getExceptions());

        for( var bc : body ){
            if( bc instanceof MCode )build((MCode) bc);
            else if( bc instanceof MEnd )build((MEnd) bc);
            else if( bc instanceof MLabel )build((MLabel) bc);
            else if( bc instanceof MLineNumber )build((MLineNumber) bc);
            else if( bc instanceof MVarInsn )build((MVarInsn) bc);
            else if( bc instanceof MInvokeDynamicInsn )build((MInvokeDynamicInsn) bc);
            else if( bc instanceof MInsn )build((MInsn) bc);
            else if( bc instanceof MLocalVariable )build((MLocalVariable) bc);
            else if( bc instanceof MMaxs )build((MMaxs) bc);
            else if( bc instanceof MFrame )build((MFrame) bc);
            else if( bc instanceof MJumpInsn )build((MJumpInsn) bc);
            else if( bc instanceof MMethodInsn )build((MMethodInsn) bc);
            else if( bc instanceof MIntInsn )build((MIntInsn) bc);
            else if( bc instanceof MLdcInsn )build((MLdcInsn) bc);
            else if( bc instanceof MTypeInsn )build((MTypeInsn) bc);
            else if( bc instanceof MFieldInsn )build((MFieldInsn) bc);
            else if( bc instanceof MIincInsn )build((MIincInsn) bc);
            else if( bc instanceof MTryCatchBlock )build((MTryCatchBlock) bc);
            else if( bc instanceof MMultiANewArrayInsn )build((MMultiANewArrayInsn) bc);
            else if( bc instanceof MLookupSwitchInsn )build((MLookupSwitchInsn) bc);
            else if( bc instanceof MAnnotationDefault )build((MAnnotationDefault) bc);
            else if( bc instanceof MAnnotation )build((MAnnotation) bc);
            else if( bc instanceof MTypeAnnotation )build((MTypeAnnotation) bc);
            else if( bc instanceof MAnnotableParameterCount )build((MAnnotableParameterCount) bc);
            else if( bc instanceof MParameterAnnotation )build((MParameterAnnotation) bc);
            else if( bc instanceof MParameter )build((MParameter) bc);
            else if( bc instanceof MInsnAnnotation )build((MInsnAnnotation) bc);
            else if( bc instanceof MTryCatchAnnotation )build((MTryCatchAnnotation) bc);
            else if( bc instanceof MLocalVariableAnnotation )build((MLocalVariableAnnotation) bc);
            else throw new UnsupportedOperationException("write "+bc);
        }
    }

    protected transient Map<String, String> targetHandles;

    private static String idOf( MHandle h ){
        return h.getName()+"|"+h.getDesc();
    }

    protected void build(MAnnotationDefault e){
        var av = mv.visitAnnotationDefault();
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
    protected void build(MAnnotation e){
        var av = mv.visitAnnotation(e.getDescriptor(),e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
    protected void build(MTypeAnnotation e){
        var av = mv.visitTypeAnnotation(
            e.getTypeRef(),
            e.getTypePath()!=null ? TypePath.fromString(e.getTypePath()) : null,
            e.getDescriptor(),e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
    protected void build(MAnnotableParameterCount e){
        mv.visitAnnotableParameterCount(e.getParameterCount(), e.isVisible());
    }
    protected void build(MParameterAnnotation e){
        var av = mv.visitParameterAnnotation(e.getParameter(),e.getDescriptor(),e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
    protected void build(MParameter ann){
        mv.visitParameter(ann.getName(),ann.getAccess());
    }
    protected void build(MInsnAnnotation e){
        var av = mv.visitInsnAnnotation(
            e.getTypeRef(),
            e.getTypePath()!=null ? TypePath.fromString(e.getTypePath()) : null,
            e.getDescriptor(),e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
    protected void build(MTryCatchAnnotation e){
        var av = mv.visitTryCatchAnnotation(
            e.getTypeRef(),
            e.getTypePath()!=null ? TypePath.fromString(e.getTypePath()) : null,
            e.getDescriptor(),e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
    protected void build(MLocalVariableAnnotation e){
        var av = mv.visitLocalVariableAnnotation(
            e.getTypeRef(),
            e.getTypePath()!=null ? TypePath.fromString(e.getTypePath()) : null,
            Arrays.stream(e.getStartLabels()).map(
                l -> labels.computeIfAbsent(l, x -> { throw new Error(""); } )
            ).toArray(Label[]::new),
            Arrays.stream(e.getEndLabels()).map(
                l -> labels.computeIfAbsent(l, x -> { throw new Error(""); } )
            ).toArray(Label[]::new),
            e.getIndex(),
            e.getDescriptor(),
            e.isVisible()
        );
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }

    protected void build(MCode code){ mv.visitCode(); }
    protected void build(MEnd end){ mv.visitEnd(); }
    protected void build(MTypeInsn tinst){
        mv.visitTypeInsn(tinst.getOpcode(), tinst.getOperand());
    }
    protected void build(MLabel lbl){ mv.visitLabel( labels.computeIfAbsent(lbl.getName(), n -> new org.objectweb.asm.Label()) ); }
    protected void build(MLineNumber ln){
        var lbl = labels.computeIfAbsent(ln.getLabel(), n -> new org.objectweb.asm.Label());
        mv.visitLineNumber(ln.getLine(),lbl);
    }
    protected void build(MVarInsn insn){ mv.visitVarInsn(insn.getOpcode(),insn.getVariable()); }
    protected void build(MInvokeDynamicInsn idi){
        var hdl = new org.objectweb.asm.Handle(
            idi.getBootstrapMethodHandle().getTag(),
            idi.getBootstrapMethodHandle().getOwner(),
            idi.getBootstrapMethodHandle().getName(),
            idi.getBootstrapMethodHandle().getDesc(),
            idi.getBootstrapMethodHandle().isIface()
        );
        Object[] args = new Object[idi.getBootstrapMethodArguments().size()];
        for( int ai=0; ai<args.length; ai++ ){
            Object arg = null;
            var sarg = idi.getBootstrapMethodArguments().get(ai);
            if( sarg instanceof IntArg ){
                arg = buildArg((IntArg) sarg);
            }else if( sarg instanceof StringArg ){
                arg = buildArg((StringArg)sarg);
            }else if( sarg instanceof FloatArg ){
                arg = buildArg((FloatArg) sarg);
            }else if( sarg instanceof LongArg ){
                arg = buildArg((LongArg) sarg);
            }else if( sarg instanceof DoubleArg ){
                arg = buildArg((DoubleArg) sarg);
            }else if( sarg instanceof TypeArg ){
                arg = buildArg((TypeArg)sarg);
            }else if( sarg instanceof HandleArg ){
                arg = buildArg((HandleArg)sarg);
            }else {
                throw new UnsupportedOperationException("can't feetch BootstrapMethodArgument from "+sarg);
            }
            args[ai] = arg;
        }
        mv.visitInvokeDynamicInsn(idi.getName(),idi.getDescriptor(),hdl,args);
    }
    protected Object buildArg(IntArg arg){ return arg.getValue(); }
    protected Object buildArg(LongArg arg){ return arg.getValue(); }
    protected Object buildArg(FloatArg arg){ return arg.getValue(); }
    protected Object buildArg(DoubleArg arg){ return arg.getValue(); }
    protected Object buildArg(StringArg arg){ return arg.getValue(); }
    protected Object buildArg(TypeArg arg){ return Type.getType(arg.getType()); }
    protected Object buildArg(HandleArg arg){
        var hdl = arg.getHandle();
        if( hdl==null )throw new IllegalArgumentException("target handle is null");

        String id = idOf(hdl);
        String name  = targetHandles.getOrDefault(id,hdl.getName());

        var binClassName = definition.getName();
        String owner = targetHandles.containsKey(id) ? binClassName : hdl.getOwner();

        var h = new org.objectweb.asm.Handle(
            hdl.getTag(),
            owner,
            name,
            hdl.getDesc(),
            hdl.isIface()
        );

        return h;
    }
    protected void build(MInsn insn){ mv.visitInsn(insn.getOpcode()); }
    protected void build(MLocalVariable lv){
        org.objectweb.asm.Label l1 = null;
        if( lv.getLabelStart()!=null )
            if( !labels.containsKey(lv.getLabelStart()) )
                throw new IllegalArgumentException("label "+lv.getLabelStart()+" not found");
            else
                l1 = labels.get(lv.getLabelStart());
        org.objectweb.asm.Label l2 = null;
        if( lv.getLabelEnd()!=null )
            if( !labels.containsKey(lv.getLabelEnd()) )
                throw new IllegalArgumentException("label "+lv.getLabelEnd()+" not found");
            else
                l2 = labels.get(lv.getLabelEnd());
        mv.visitLocalVariable(lv.getName(),lv.getDescriptor(),lv.getSignature(),l1,l2,lv.getIndex());
    }
    protected void build(MMaxs m){ mv.visitMaxs(m.getMaxStack(), m.getMaxLocals()); }
    protected void build(MFrame f){
        Object[] local = f.getLocal()==null ? null : new Object[f.getLocal().size()];
        if( local!=null ){
            for( int i=0;i<local.length;i++ ){
                var v = f.getLocal().get(i);
                if( v==null ){
                    local[i] = null;
                }else {
                    switch( v ){
                        case Top: local[i]= Opcodes.TOP; break;
                        case Float: local[i]=Opcodes.FLOAT; break;
                        case Double: local[i]=Opcodes.DOUBLE; break;
                        case Integer: local[i]=Opcodes.INTEGER; break;
                        case Null: local[i]=Opcodes.NULL; break;
                        case UninitializedThis: local[i]=Opcodes.UNINITIALIZED_THIS; break;
                        default:
                            throw new IllegalArgumentException("unsupported frame local "+v);
                    }
                }
            }
        }
        Object[] stack = f.getStack()==null ? null : new Object[f.getStack().size()];
        if( stack!=null ){
            for( int i=0;i<stack.length;i++ ){
                var v = f.getStack().get(i);
                if( v==null ){
                    stack[i] = null;
                }else {
                    switch( v ){
                        case Top: stack[i]=Opcodes.TOP; break;
                        case Float: stack[i]=Opcodes.FLOAT; break;
                        case Double: stack[i]=Opcodes.DOUBLE; break;
                        case Integer: stack[i]=Opcodes.INTEGER; break;
                        case Null: stack[i]=Opcodes.NULL; break;
                        case UninitializedThis: stack[i]=Opcodes.UNINITIALIZED_THIS; break;
                        default:
                            throw new IllegalArgumentException("unsupported frame local "+v);
                    }
                }
            }
        }
        mv.visitFrame(f.getType(),f.getNumLocal(),local,f.getNumStack(),stack);
    }
    protected void build(MJumpInsn j){
        if( j.getLabel()==null )throw new IllegalArgumentException("jump label not defined");

        var lbl = labels.computeIfAbsent(j.getLabel(), l -> new org.objectweb.asm.Label());
        mv.visitJumpInsn(j.getOpcode(),lbl);
    }
    protected void build(MMethodInsn mi){
        mv.visitMethodInsn(mi.getOpcode(),mi.getOwner(),mi.getName(),mi.getDescriptor(),mi.isIface());
    }
    protected void build(MIntInsn ii){
        mv.visitIntInsn(ii.getOpcode(), ii.getOperand());
    }
    protected void build(MLdcInsn ldc){
        switch( ldc.getLdcType() ){
            case Long: mv.visitLdcInsn((Long)ldc.getValue()); break;
            case Integer: mv.visitLdcInsn((Integer)ldc.getValue()); break;
            case Double: mv.visitLdcInsn((Double)ldc.getValue()); break;
            case String: mv.visitLdcInsn((String)ldc.getValue()); break;
            case Float: mv.visitLdcInsn((Float)ldc.getValue()); break;
            case Handle:
                var hdl1 = (MHandle)ldc.getValue();
                var hdl0 = new org.objectweb.asm.Handle(
                    hdl1.getTag(), hdl1.getOwner(), hdl1.getName(), hdl1.getDesc(), hdl1.isIface()
                );
                mv.visitLdcInsn(hdl0);
                break;
            default:
                throw new UnsupportedOperationException("not impl for ldc type = "+ldc.getLdcType());
        }
    }
    protected void build(MFieldInsn fld){
        mv.visitFieldInsn(fld.getOpcode(), fld.getOwner(), fld.getName(), fld.getDescriptor());
    }
    protected void build(MIincInsn ii){
        mv.visitIincInsn(ii.getVariable(), ii.getIncrement());
    }
    protected void build(MTryCatchBlock tcb){
        var start = tcb.getLabelStart()!=null ? labels.computeIfAbsent(
            tcb.getLabelStart(),
            l -> {
                throw new IllegalArgumentException("label "+l+" not found");
            }
        ) : null;
        var handler = tcb.getLabelHandler()!=null ? labels.computeIfAbsent(
            tcb.getLabelHandler(),
            l -> {
                throw new IllegalArgumentException("label "+l+" not found");
            }
        ) : null;
        var end = tcb.getLabelEnd()!=null ? labels.computeIfAbsent(
            tcb.getLabelEnd(),
            l -> {
                throw new IllegalArgumentException("label "+l+" not found");
            }
        ) : null;
        mv.visitTryCatchBlock(start,end,handler,tcb.getType());
    }
    protected void build(MMultiANewArrayInsn ii){
        mv.visitMultiANewArrayInsn(ii.getDescriptor(), ii.getNumDimensions());
    }
    protected void build(MLookupSwitchInsn lsw){
        var defLbl = lsw.getDefaultHandlerLabel()!=null
            ? labels.computeIfAbsent(
            lsw.getDefaultHandlerLabel(),
            l -> {
                throw new IllegalArgumentException("label "+l+" not found");
            })
            : null;
        var lbls = lsw.getLabels()!=null
            ? List.of(lsw.getLabels()).stream().map( lbl ->
            lbl!=null
                ? labels.computeIfAbsent(
                lbl,
                l -> {
                    throw new IllegalArgumentException("label "+l+" not found");
                })
                : null
        ).toArray(org.objectweb.asm.Label[]::new)
            : null;
        mv.visitLookupSwitchInsn(defLbl,lsw.getKeys(),lbls);
    }
    protected void build(MTableSwitchInsn tsw){
        var defLbl = tsw.getDefaultLabel()!=null
            ? labels.computeIfAbsent(
            tsw.getDefaultLabel(),
            l -> {
                throw new IllegalArgumentException("label "+l+" not found");
            })
            : null;
        var lbls = tsw.getLabels()!=null
            ? List.of(tsw.getLabels()).stream().map( lbl ->
            lbl!=null
                ? labels.computeIfAbsent(
                lbl,
                l -> {
                    throw new IllegalArgumentException("label "+l+" not found");
                })
                : null
        ).toArray(org.objectweb.asm.Label[]::new)
            : null;
        mv.visitTableSwitchInsn(tsw.getMin(), tsw.getMax(), defLbl, lbls);
    }
}
