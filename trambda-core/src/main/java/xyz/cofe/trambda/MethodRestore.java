package xyz.cofe.trambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.*;

public class MethodRestore {
    public MethodRestore(){
    }

    public MethodRestore(MethodRestore sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.className = sample.className;
        this.methodDef = sample.methodDef;
        this.methodName = sample.methodName;
        this.cw = sample.cw;
        this.mv = sample.mv;
        this.labels = sample.labels;
    }

    public synchronized MethodRestore clone(){
        return new MethodRestore(this);
    }

    //region className : String
    private String className;
    public synchronized String getClassName(){ return className; }
    public synchronized void setClassName(String className){ this.className = className; }
    public MethodRestore className(String name){
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
    public MethodRestore methodDef(MethodDef mdef){
        setMethodDef(mdef);
        return this;
    }
    //endregion
    //region methodName : String
    private String methodName;
    public synchronized String getMethodName(){return methodName;}
    public synchronized void setMethodName(String mname){ methodName = mname; }
    public MethodRestore methodName(String mname){
        setMethodName(mname);
        return this;
    }
    //endregion

    protected ClassWriter cw;
    protected MethodVisitor mv;
    protected Map<String, org.objectweb.asm.Label> labels;
    protected Map<String, String> targetHandles;
    protected Map<String, MethodDef> targetMethodDef;
    protected String binClassName;

    private static String idOf( MethodDef mf ){
        return mf.getName()+"|"+mf.getDescriptor();
    }

    private static String idOf( Handle h ){
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
            if( bc instanceof Code )build((Code) bc);
            else if( bc instanceof End )build((End) bc);
            else if( bc instanceof Label )build((Label) bc);
            else if( bc instanceof LineNumber )build((LineNumber) bc);
            else if( bc instanceof VarInsn )build((VarInsn) bc);
            else if( bc instanceof InvokeDynamicInsn )build((InvokeDynamicInsn) bc);
            else if( bc instanceof Insn )build((Insn) bc);
            else if( bc instanceof LocalVariable )build((LocalVariable) bc);
            else if( bc instanceof Maxs )build((Maxs) bc);
            else if( bc instanceof Frame )build((Frame) bc);
            else if( bc instanceof JumpInsn )build((JumpInsn) bc);
            else if( bc instanceof MethodInsn )build((MethodInsn) bc);
            else if( bc instanceof IntInsn )build((IntInsn) bc);
            else if( bc instanceof LdcInsn )build((LdcInsn) bc);
            else if( bc instanceof TypeInsn )build((TypeInsn) bc);
            else if( bc instanceof FieldInsn )build((FieldInsn) bc);
        }
    }

    protected void build(Code code){ mv.visitCode(); }
    protected void build(End end){ mv.visitEnd(); }
    protected void build(TypeInsn tinst){
        mv.visitTypeInsn(tinst.getOpcode(), tinst.getOperand());
    }
    protected void build(Label lbl){ mv.visitLabel( labels.computeIfAbsent(lbl.getName(), n -> new org.objectweb.asm.Label()) ); }
    protected void build(LineNumber ln){
        var lbl = labels.computeIfAbsent(ln.getLabel(), n -> new org.objectweb.asm.Label());
        mv.visitLineNumber(ln.getLine(),lbl);
    }
    protected void build(VarInsn insn){ mv.visitVarInsn(insn.getOpcode(),insn.getVariable()); }
    protected void build(InvokeDynamicInsn idi){
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
    protected void build(Insn insn){ mv.visitInsn(insn.getOpcode()); }
    protected void build(LocalVariable lv){
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
    protected void build(Maxs m){ mv.visitMaxs(m.getMaxStack(), m.getMaxLocals()); }
    protected void build(Frame f){
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
    protected void build(JumpInsn j){
        if( j.getLabel()==null )throw new IllegalArgumentException("jump label not defined");

        var lbl = labels.computeIfAbsent(j.getLabel(), l -> new org.objectweb.asm.Label());
        mv.visitJumpInsn(j.getOpcode(),lbl);
    }
    protected void build(MethodInsn mi){
        mv.visitMethodInsn(mi.getOpcode(),mi.getOwner(),mi.getName(),mi.getDescriptor(),mi.isIface());
    }
    protected void build(IntInsn ii){
        mv.visitIntInsn(ii.getOpcode(), ii.getOperand());
    }
    protected void build(LdcInsn ldc){
        switch( ldc.getLdcType() ){
            case Long: mv.visitLdcInsn((Long)ldc.getValue()); break;
            case Integer: mv.visitLdcInsn((Integer)ldc.getValue()); break;
            case Double: mv.visitLdcInsn((Double)ldc.getValue()); break;
            case String: mv.visitLdcInsn((String)ldc.getValue()); break;
            case Float: mv.visitLdcInsn((Float)ldc.getValue()); break;
            case Handle:
                var hdl1 = (Handle)ldc.getValue();
                var hdl0 = new org.objectweb.asm.Handle(
                    hdl1.getTag(), hdl1.getOwner(), hdl1.getName(), hdl1.getDesc()
                );
                mv.visitLdcInsn(hdl0);
                break;
            default:
                throw new UnsupportedOperationException("not impl for ldc type = "+ldc.getLdcType());
        }
    }
    protected void build(FieldInsn fld){
        mv.visitFieldInsn(fld.getOpcode(), fld.getOwner(), fld.getName(), fld.getDescriptor());
    }
}
