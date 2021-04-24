package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.mth.MInvokeDynamicInsn;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.bc.mth.MMethodInsn;

public abstract class Invoke<INSTR extends ByteCode> extends SecurAccess<INSTR, MethodDef> {
    public Invoke(INSTR instr, MethodDef mdef){
        super(instr,mdef);
    }

    public static List<Invoke<?>> inspectCall(MethodDef mdef){
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        List<Invoke<?>> result = new ArrayList<>();
        inspectCall(result,mdef,null);
        return result;
    }
    private static void inspectCall(List<Invoke<?>> result, MethodDef mdef, Set<MethodDef> visited){
        if( result==null )throw new IllegalArgumentException( "result==null" );
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        if( visited==null )visited = new HashSet<>();
        if( visited.contains(mdef) )return;
        visited.add(mdef);

        var byteCodes = mdef.getByteCodes();
        if( byteCodes!=null ){
            for( var bc : mdef.getByteCodes() ){
                if( bc==null )continue;
                if( bc instanceof MInvokeDynamicInsn ){
                    result.add(new InvokeDynamicCall((MInvokeDynamicInsn) bc, mdef));
                }else if( bc instanceof MMethodInsn ){
                    result.add(new InvokeMethod((MMethodInsn) bc, mdef));
                }
            }
        }

        var r = mdef.getRefs();
        if( r!=null ){
            for( var m : r ){
                if( m!=null ){
                    inspectCall(result, m, visited);
                }
            }
        }
    }

    public abstract String getOwner();
    public abstract String getMethodName();
    public abstract MethodDescTypes getMethodTypes();
}
