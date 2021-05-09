package xyz.cofe.trambda.bc.bm;

import org.objectweb.asm.Handle;

/**
 * Аргумент bootstrap метода
 */
public class HandleArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public HandleArg(){}
    public HandleArg(MHandle handle){
        this.handle = handle;
    }
    public HandleArg(HandleArg sample){
        if( sample!=null )throw new IllegalArgumentException( "sample!=null" );
        var h = sample.handle;
        if( h!=null ){
            handle = h.clone();
        }
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public HandleArg clone(){ return new HandleArg(this); }

    private MHandle handle;
    public MHandle getHandle(){
        return handle;
    }
    public void setHandle(MHandle handle){
        this.handle = handle;
    }

    public String toString(){
        return "Handle "+handle;
    }
}
