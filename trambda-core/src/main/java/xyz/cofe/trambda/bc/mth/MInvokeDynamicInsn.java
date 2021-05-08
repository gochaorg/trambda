package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.LongArg;

public class MInvokeDynamicInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MInvokeDynamicInsn(){
    }

    public MInvokeDynamicInsn(
        String name,
        String descriptor,
        org.objectweb.asm.Handle handle,
        Object[] args
    ){
        this.name = name;
        this.descriptor = descriptor;
        if( handle!=null ){
            this.bootstrapMethodHandle = new MHandle(handle);
        }

        if(args!=null){
            bootstrapMethodArguments = new ArrayList<>();
            for( var a : args ){
                if( a instanceof Integer ){
                    bootstrapMethodArguments.add(new IntArg((Integer) a));
                }else if( a instanceof Float ){
                    bootstrapMethodArguments.add(new FloatArg((Float) a));
                }else if( a instanceof Long ){
                    bootstrapMethodArguments.add(new LongArg((Long) a));
                }else if( a instanceof Double ){
                    bootstrapMethodArguments.add(new DoubleArg((Double) a));
                }else if( a instanceof String ){
                    bootstrapMethodArguments.add(new StringArg((String) a));
                }else if( a instanceof Type ){
                    bootstrapMethodArguments.add(new TypeArg(((Type)a).toString()));
                }else if( a instanceof org.objectweb.asm.Handle ){
                    bootstrapMethodArguments.add(new HandleArg(new MHandle((org.objectweb.asm.Handle)a)));
                }else {
                    throw new IllegalArgumentException("unsupported bootstrapMethodArgument "+a);
                }
            }
        }
    }

    //region name : String
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String
    private String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region bootstrapMethodHandle : MHandle
    private MHandle bootstrapMethodHandle;
    public MHandle getBootstrapMethodHandle(){
        return bootstrapMethodHandle;
    }
    public void setBootstrapMethodHandle(MHandle bootstrapMethodHandle){
        this.bootstrapMethodHandle = bootstrapMethodHandle;
    }
    //endregion
    //region bootstrapMethodArguments : List<BootstrapMethArg>
    private List<BootstrapMethArg> bootstrapMethodArguments;
    public List<BootstrapMethArg> getBootstrapMethodArguments(){
        if( bootstrapMethodArguments==null ){
            bootstrapMethodArguments = new ArrayList<>();
        }
        return bootstrapMethodArguments;
    }
    public void setBootstrapMethodArguments(List<BootstrapMethArg> bootstrapMethodArguments){
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
    //endregion

    public String toString(){
        return MInvokeDynamicInsn.class.getSimpleName()+
            " name="+name+
            " descriptor="+descriptor+
            " bootstrapMethodHandle="+bootstrapMethodHandle+
            " args="+bootstrapMethodArguments+"";
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var bmh = getBootstrapMethodHandle();
        if( bmh==null )throw new IllegalStateException("getBootstrapMethodHandle() return null");

        var hdl = new org.objectweb.asm.Handle(
            bmh.getTag(),
            bmh.getOwner(),
            bmh.getName(),
            bmh.getDesc(),
            bmh.isIface()
        );

        var bma = getBootstrapMethodArguments();
        if( bma==null ){
            throw new IllegalStateException("getBootstrapMethodArguments() return null");
        }

        Object[] args = new Object[bma.size()];
        for( int ai=0; ai<args.length; ai++ ){
            Object arg = null;
            var sarg = bma.get(ai);
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
                arg = build((HandleArg)sarg, hdl, ctx);
            }else {
                throw new UnsupportedOperationException("can't feetch BootstrapMethodArgument from "+sarg);
            }
            args[ai] = arg;
        }

        v.visitInvokeDynamicInsn(getName(),getDescriptor(),hdl,args);
    }

    protected Object build(IntArg arg){ return arg.getValue(); }
    protected Object build(LongArg arg){ return arg.getValue(); }
    protected Object build(FloatArg arg){ return arg.getValue(); }
    protected Object build(DoubleArg arg){ return arg.getValue(); }
    protected Object build(StringArg arg){ return arg.getValue(); }
    protected Object build(TypeArg arg){ return Type.getType(arg.getType()); }
    protected Object build(HandleArg arg, org.objectweb.asm.Handle bm, MethodWriterCtx ctx){
        var hdl = arg.getHandle();
        if( hdl==null )throw new IllegalArgumentException("target handle is null");
        return ctx.bootstrapArgument(hdl, bm);
    }
}
