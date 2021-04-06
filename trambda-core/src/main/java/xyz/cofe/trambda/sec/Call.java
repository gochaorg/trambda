package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.InvokeDynamicInsn;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.bc.MethodInsn;

public abstract class Call<INSTR extends ByteCode> extends SecurAccess<INSTR, MethodDef> {
    public Call(INSTR instr, MethodDef mdef){
        super(instr,mdef);
    }

    public static List<Call<?>> inspectCall(MethodDef mdef){
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        List<Call<?>> result = new ArrayList<>();
        inspectCall(result,mdef,null);
        return result;
    }
    private static void inspectCall(List<Call<?>> result, MethodDef mdef, Set<MethodDef> visited){
        if( result==null )throw new IllegalArgumentException( "result==null" );
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        if( visited==null )visited = new HashSet<>();
        if( visited.contains(mdef) )return;
        visited.add(mdef);

        var byteCodes = mdef.getByteCodes();
        if( byteCodes!=null ){
            for( var bc : mdef.getByteCodes() ){
                if( bc==null )continue;
                if( bc instanceof InvokeDynamicInsn ){
                    result.add(new InvokeDynamicCall((InvokeDynamicInsn) bc, mdef));
                }else if( bc instanceof MethodInsn ){
                    result.add(new MethodCall((MethodInsn) bc, mdef));
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
