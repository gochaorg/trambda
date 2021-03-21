package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.tcp.Message;

public class Compile implements Message {
    private MethodDef methodDef;

    public MethodDef getMethodDef(){
        return methodDef;
    }

    public void setMethodDef(MethodDef methodDef){
        this.methodDef = methodDef;
    }
}
