package xyz.cofe.trambda.bc.bm;

import xyz.cofe.trambda.bc.mth.MHandle;

/**
 * Аргумент bootstrap метода
 */
public class HandleArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public HandleArg(){}
    public HandleArg(MHandle handle){
        this.handle = handle;
    }

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
