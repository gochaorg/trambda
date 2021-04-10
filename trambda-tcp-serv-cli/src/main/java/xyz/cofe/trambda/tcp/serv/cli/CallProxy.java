package xyz.cofe.trambda.tcp.serv.cli;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.Invoke;
import xyz.cofe.trambda.sec.MethodDescTypes;

public class CallProxy {
    public final Invoke<?> invoke;
    public CallProxy(Invoke<?> invoke){
        this.invoke = invoke;
    }

    public String getMethodOwner(){
        return invoke.getOwner();
    }

    public String getMethodName(){
        return invoke.getMethodName();
    }

    public MethodDescTypes getMethodTypes(){
        return invoke.getMethodTypes();
    }

    public MethodDef getScope(){
        return invoke.getScope();
    }

    public ByteCode getInstruction(){
        return invoke.getInstruction();
    }
}
