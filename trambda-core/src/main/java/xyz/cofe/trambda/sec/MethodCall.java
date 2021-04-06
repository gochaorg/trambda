package xyz.cofe.trambda.sec;

import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.bc.MethodInsn;

public class MethodCall extends Call<MethodInsn> {
    public MethodCall(MethodInsn methodInsn, MethodDef mdef){
        super(methodInsn, mdef);
    }

    public MethodCall(MethodCall sample){
        super(sample.instruction, sample.scope);
    }

    @Override
    public MethodCall clone(){
        return new MethodCall(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb
            .append("method")
            .append(" iface=")
            .append(instruction.isIface())
            .append(" owner=").append(getOwner())
            .append(" method.name=").append(instruction.getName())
            .append(" method.type=").append(MethodDescTypes.parse(instruction.getDescriptor()))
            ;
        return sb.toString();
    }

    public String getOwner(){
        if( instruction.getOwner()!=null ){
            return instruction.getOwner().replace("/",".");
        }
        return "?";
    }

    public String getMethodName(){
        return instruction.getName();
    }

    public MethodDescTypes getMethodTypes(){
        return MethodDescTypes.parse(instruction.getDescriptor());
    }
}
