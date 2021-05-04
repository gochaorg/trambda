package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.LongArg;

public class MInvokeDynamicInsn extends MAbstractBC implements ByteCode {
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
        return MInvokeDynamicInsn.class.getSimpleName()+" {name="+name+" descriptor="+descriptor+" "+bootstrapMethodHandle+" arg="+bootstrapMethodArguments+"}";
    }
}
