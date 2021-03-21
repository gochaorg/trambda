package xyz.cofe.trambda.bc;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Type;

public class InvokeDynamicInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public InvokeDynamicInsn(){
    }

    public InvokeDynamicInsn(
        String name,
        String descriptor,
        org.objectweb.asm.Handle handle,
        Object[] args
    ){
        this.name = name;
        this.descriptor = descriptor;
        if( handle!=null ){
            this.bootstrapMethodHandle = new Handle(handle);
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
                    bootstrapMethodArguments.add(new HandleArg(new Handle((org.objectweb.asm.Handle)a)));
                }else {
                    throw new IllegalArgumentException("unsupported bootstrapMethodArgument "+a);
                }
            }
        }
    }

    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    private String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }

    private Handle bootstrapMethodHandle;
    public Handle getBootstrapMethodHandle(){
        return bootstrapMethodHandle;
    }
    public void setBootstrapMethodHandle(Handle bootstrapMethodHandle){
        this.bootstrapMethodHandle = bootstrapMethodHandle;
    }

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

    public String toString(){
        return "InvokeDynamicInsn {name="+name+" descriptor="+descriptor+" "+bootstrapMethodHandle+" arg="+bootstrapMethodArguments+"}";
    }
}
