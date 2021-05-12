package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.mth.MFieldInsn;
import xyz.cofe.trambda.bc.mth.OpCode;

public class FieldAccess extends SecurAccess<MFieldInsn, Tuple2<LambdaDump, LambdaNode>> {
    public FieldAccess(MFieldInsn fieldInsn, Tuple2<LambdaDump, LambdaNode> methodDef){
        super(fieldInsn, methodDef);
    }

    public FieldAccess(FieldAccess sample){
        super(sample.instruction, sample.scope);
    }

    @Override
    public FieldAccess clone(){
        return new FieldAccess(this);
    }

    public static List<FieldAccess> inspectField(LambdaDump mdef ){
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        List<FieldAccess> result = new ArrayList<>();
        inspectField(result,mdef);
        return result;
    }
    private static void inspectField(List<FieldAccess> result, LambdaDump dump){
        if( dump==null )throw new IllegalArgumentException( "dump==null" );
        if( result==null )throw new IllegalArgumentException( "result==null" );

        var lnode = dump.getLambdaNode();
        if( lnode==null )throw new IllegalArgumentException("dump.getLambdaNode()==null");

        lnode.walk().tree().forEach( t -> {
            var node = t.getNode();
            var meth = node.getMethod();
            var byteCodes = meth!=null ? meth.getMethodByteCodes() : null;
            if( byteCodes!=null ){
                for( var bc : byteCodes ){
                    if( bc==null )continue;

                    if( bc instanceof MFieldInsn ){
                        result.add(new FieldAccess((MFieldInsn)bc, Tuple2.of(dump,node)) );
                    }
                }
            }
        });
    }

    public String getOwner(){
        if( instruction.getOwner()!=null ){
            return instruction.getOwner().replace("/",".");
        }
        return "?";
    }
    public String getFieldName(){
        if( instruction.getName()!=null ){
            return instruction.getName();
        }
        return "?";
    }
    public TypeDesc getFieldType(){
        if( instruction.getDescriptor()!=null ){
            return TypeDesc.parse(instruction.getDescriptor());
        }
        return TypeDesc.undefined;
    }

    public static enum Operation {
        Undefined(-1),
        GetStatic(OpCode.GETSTATIC.code),
        PutStatic(OpCode.PUTSTATIC.code),
        GetField(OpCode.GETFIELD.code),
        PutField(OpCode.PUTFIELD.code);
        public final int opcode;
        Operation(int opcode){
            this.opcode = opcode;
        }
    }
    public Operation getOperation(){
        for( var o : Operation.values() ){
            if( o.opcode==instruction.getOpcode() ){
                return o;
            }
        }
        return Operation.Undefined;
    }

    public boolean isStatic(){
        return instruction.getOpcode()==OpCode.GETSTATIC.code || instruction.getOpcode()==OpCode.PUTSTATIC.code;
    }
    public boolean isField(){
        return instruction.getOpcode()==OpCode.GETFIELD.code || instruction.getOpcode()==OpCode.PUTFIELD.code;
    }
    public boolean isReadAccess(){
        return instruction.getOpcode()==OpCode.GETSTATIC.code || instruction.getOpcode()==OpCode.GETFIELD.code;
    }
    public boolean isWriteAccess(){
        return instruction.getOpcode()==OpCode.PUTSTATIC.code || instruction.getOpcode()==OpCode.PUTFIELD.code;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("field");
        if( isStatic() ){
            sb.append(" static");
        }else {
        }
        if( isReadAccess() ){
            sb.append(" read");
        }else if( isWriteAccess() ){
            sb.append(" write");
        }
        sb.append(" ").append(getFieldName()).append(" : ").append(getFieldType());
        sb.append(" of ").append(getOwner());
        return sb.toString();
    }
}
