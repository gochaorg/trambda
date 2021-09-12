package xyz.cofe.trambda.sec;

import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.mth.MInvokeDynamicInsn;

/**
 * Инструкция вызова bootstrap метода
 */
public class InvokeDynamicCall extends Invoke<MInvokeDynamicInsn> {
    public InvokeDynamicCall(MInvokeDynamicInsn invokeDynamicInsn, Tuple2<LambdaDump,LambdaNode> scope){
        super(invokeDynamicInsn, scope);
    }

    public InvokeDynamicCall(InvokeDynamicCall sample){
        super(sample.instruction, sample.scope);
    }

    @Override
    public InvokeDynamicCall clone(){
        return new InvokeDynamicCall(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        var h = instruction.getBootstrapMethodHandle();
        sb.append("indy");
        if( h!=null ){
            sb.append(" owner=").append(getOwner());
            sb.append(" method.name=").append(h.getName());
            sb.append(" method.type=").append(MethodDescTypes.parse(h.getDesc()));
        }
        return sb.toString();
    }

    public String getOwner(){
        var h = instruction.getBootstrapMethodHandle();
        if( h!=null ){
            return h.getOwner().replace("/",".");
        }
        return "?";
    }

    public String getMethodName(){
        var h = instruction.getBootstrapMethodHandle();
        if( h!=null ){
            return h.getName();
        }
        return "?";
    }

    public MethodDescTypes getMethodTypes(){
        var h = instruction.getBootstrapMethodHandle();
        if( h!=null ){
            return MethodDescTypes.parse(h.getDesc());
        }
        return MethodDescTypes.undefined;
    }
}
