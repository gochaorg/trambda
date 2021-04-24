package xyz.cofe.trambda.sec;

import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.bc.mth.MMethodInsn;

public class InvokeMethod extends Invoke<MMethodInsn> {
    public InvokeMethod(MMethodInsn methodInsn, MethodDef mdef){
        super(methodInsn, mdef);
    }

    public InvokeMethod(InvokeMethod sample){
        super(sample.instruction, sample.scope);
    }

    @Override
    public InvokeMethod clone(){
        return new InvokeMethod(this);
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
