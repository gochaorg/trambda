package xyz.cofe.trambda.bc;

/**
 * Аргумент bootstrap метода
 */
public class HandleArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public HandleArg(){}
    public HandleArg(Handle handle){
        this.handle = handle;
    }

    private Handle handle;
    public Handle getHandle(){
        return handle;
    }
    public void setHandle(Handle handle){
        this.handle = handle;
    }

    public String toString(){
        return "Handle "+handle;
    }
}
