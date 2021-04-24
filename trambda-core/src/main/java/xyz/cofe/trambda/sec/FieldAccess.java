package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import xyz.cofe.trambda.bc.mth.MFieldInsn;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.bc.mth.OpCode;

public class FieldAccess extends SecurAccess<MFieldInsn,MethodDef> {
    public FieldAccess(MethodDef methodDef, MFieldInsn fieldInsn){
        super(fieldInsn, methodDef);
    }

    public FieldAccess(FieldAccess sample){
        super(sample.instruction, sample.scope);
    }

    @Override
    public FieldAccess clone(){
        return new FieldAccess(this);
    }

    public static List<FieldAccess> inspectField(MethodDef mdef ){
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        List<FieldAccess> result = new ArrayList<>();
        inspectField(result,mdef,null);
        return result;
    }
    private static void inspectField(List<FieldAccess> result, MethodDef mdef, Set<MethodDef> visited ){
        if( visited==null )visited = new HashSet<>();
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        if( result==null )throw new IllegalArgumentException( "result==null" );
        if( visited.contains(mdef) )return;
        visited.add(mdef);

        if( mdef.getByteCodes()!=null ){
            for( var bc : mdef.getByteCodes() ){
                if( bc instanceof MFieldInsn ){
                    result.add(new FieldAccess(mdef,(MFieldInsn)bc) );
                }
            }
        }

        if( mdef.getRefs()!=null ){
            for( var m : mdef.getRefs() ){
                if( m!=null ){
                    inspectField(result,m,visited);
                }
            }
        }
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
