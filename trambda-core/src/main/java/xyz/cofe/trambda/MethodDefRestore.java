package xyz.cofe.trambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.*;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.LongArg;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.mth.*;

/**
 * Генерация байт-кода класса из представления
 * {@link xyz.cofe.trambda.bc.MethodDef}
 *
 * <pre>
 * var byteCode = new MethodRestore()
 *      // Имя целевого класса
 *     .className("xyz.cofe.trambda.buildMethodTest.Build1")
 *     // Имя целевого метода
 *     .methodName("lambda1")
 *     // Сериализованная лямбда
 *     .methodDef(mdef)
 *     // Генерация байт кода
 *     .generate();
 * </pre>
 *
 * @see MethodDump
 * @see MethodDef
 * @see AsmQuery
 */
public class MethodDefRestore {
    /**
     * Конструктор по умолчанию
     */
    public MethodDefRestore(){
    }

    public MethodDefRestore(MethodDefRestore sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.className = sample.className;
        this.methodDef = sample.methodDef;
        this.methodName = sample.methodName;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public synchronized MethodDefRestore clone(){
        return new MethodDefRestore(this);
    }

    //region className : String
    private String className;
    public synchronized String getClassName(){ return className; }
    public synchronized void setClassName(String className){ this.className = className; }
    public MethodDefRestore className(String name){
        setClassName(name);
        return this;
    }
    //endregion
    //region methodDef : MethodDef
    private MethodDef methodDef;
    public synchronized MethodDef getMethodDef(){return methodDef;}
    public synchronized void setMethodDef(MethodDef mdef){
        this.methodDef = mdef;
    }
    public MethodDefRestore methodDef(MethodDef mdef){
        setMethodDef(mdef);
        return this;
    }
    //endregion
    //region methodName : String
    private String methodName;
    public synchronized String getMethodName(){return methodName;}
    public synchronized void setMethodName(String mname){ methodName = mname; }
    public MethodDefRestore methodName(String mname){
        setMethodName(mname);
        return this;
    }
    //endregion

    protected transient ClassWriter cw;
    protected transient MethodVisitor mv;
    protected transient Map<String, org.objectweb.asm.Label> labels;
    protected transient Map<String, String> targetHandles;
    protected transient Map<String, MethodDef> targetMethodDef;
    protected transient String binClassName;

    private static String idOf( MethodDef mf ){
        return mf.getName()+"|"+mf.getDescriptor();
    }
    private static String idOf( MHandle h ){
        return h.getName()+"|"+h.getDesc();
    }
    private String targetNameOf( MethodDef mdef, int idx ){
        return "ref"+idx;
    }

    public synchronized byte[] generate(){
        if( className==null )throw new IllegalStateException( "className==null" );
        if( methodDef==null )throw new IllegalStateException( "methodDef==null" );
        binClassName = className.replace('.', '/');

        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V11,
            Opcodes.ACC_PUBLIC|Opcodes.ACC_SUPER,
            binClassName,null,
            "java/lang/Object", null
        );

        Map<String,Tuple2<MethodDef,Integer>> mdefMap = new LinkedHashMap<>();
        List<Tuple2<MethodDef,Integer>> lmdef = new ArrayList<>();
        lmdef.add(Tuple2.of(methodDef,0));
        while( !lmdef.isEmpty() ){
            var mf = lmdef.remove(0);
            String id = idOf(mf.a());
            if( !mdefMap.containsKey(id) ){
                mdefMap.put(id,mf);
                if( mf.a().getRefs()!=null ){
                    for( var ref : mf.a().getRefs() ){
                        if( ref != null ) lmdef.add(Tuple2.of(ref, mf.b()+1));
                    }
                }
            }
        }

        targetHandles = new HashMap<>();
        targetMethodDef = new HashMap<>();

        int idx = -1;
        for( var mref : mdefMap.values() ){
            if( mref.b()>0 ){
                idx++;
                String id = idOf(mref.a());
                String name = targetNameOf(mref.a(),idx);
                targetHandles.put(id,name);
                targetMethodDef.put(id,mref.a());
            }
        }

        buildConstructor();
        buildMethod();

        for( String mrefId : targetHandles.keySet() ){
            String name = targetHandles.get(mrefId);
            MethodDef mdefRef = targetMethodDef.get(mrefId);
            buildMethod(
                Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC,
                name,
                mdefRef.getDescriptor(),
                mdefRef.getSignature(),
                mdefRef.getExceptions(),
                mdefRef.getByteCodes()
            );
        }

        return cw.toByteArray();
    }

    protected void buildConstructor(){
        MethodVisitor mv;
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC,"<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD,0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1,-1);
        mv.visitEnd();
    }
    protected void buildMethod(){
        if( methodName==null )throw new IllegalStateException("methodName == null");
        if( methodDef==null )throw new IllegalStateException("methodDef == null");
        if( cw==null )throw new IllegalStateException("cw == null");

        buildMethod(
            Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC,
            methodName, methodDef.getDescriptor(), methodDef.getSignature(), methodDef.getExceptions(),
            methodDef.getByteCodes()
        );
    }
    protected void buildMethod(int acc, String name, String desc, String sign, String[] excepts, List<ByteCode> byteCodes){
        mv = cw.visitMethod(acc, name, desc, sign, excepts);

        labels = new LinkedHashMap<>();

        for( var bc : byteCodes ){
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
        }
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
                arg = build((IntArg) sarg);
            }else if( sarg instanceof StringArg ){
                arg = build((StringArg)sarg);
            }else if( sarg instanceof FloatArg ){
                arg = build((FloatArg) sarg);
            }else if( sarg instanceof LongArg ){
                arg = build((LongArg) sarg);
            }else if( sarg instanceof DoubleArg ){
                arg = build((DoubleArg) sarg);
            }else if( sarg instanceof TypeArg ){
                arg = build((TypeArg)sarg);
            }else if( sarg instanceof HandleArg ){
                arg = build((HandleArg)sarg);
            }else {
                throw new UnsupportedOperationException("can't feetch BootstrapMethodArgument from "+sarg);
            }
            args[ai] = arg;
        }
        mv.visitInvokeDynamicInsn(idi.getName(),idi.getDescriptor(),hdl,args);
    }
    protected Object build(IntArg arg){ return arg.getValue(); }
    protected Object build(LongArg arg){ return arg.getValue(); }
    protected Object build(FloatArg arg){ return arg.getValue(); }
    protected Object build(DoubleArg arg){ return arg.getValue(); }
    protected Object build(StringArg arg){ return arg.getValue(); }
    protected Object build(TypeArg arg){ return Type.getType(arg.getType()); }
    protected Object build(HandleArg arg){
        var hdl = arg.getHandle();
        if( hdl==null )throw new IllegalArgumentException("target handle is null");

        String id = idOf(hdl);
        String name  = targetHandles.getOrDefault(id,hdl.getName());
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
                        case Top: local[i]=Opcodes.TOP; break;
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
