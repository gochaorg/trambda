package xyz.cofe.trambda.tcp.serv.cli;

import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.ByteCode;
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

    public Tuple2<LambdaDump, LambdaNode> getScope(){
        return invoke.getScope();
    }

    public ByteCode getInstruction(){
        return invoke.getInstruction();
    }
}
