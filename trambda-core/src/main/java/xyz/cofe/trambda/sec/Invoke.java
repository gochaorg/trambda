package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.mth.MInvokeDynamicInsn;
import xyz.cofe.trambda.bc.mth.MMethodInsn;

public abstract class Invoke<INSTR extends ByteCode> extends SecurAccess<INSTR, Tuple2<LambdaDump,LambdaNode>> {
    public Invoke(INSTR instr, Tuple2<LambdaDump,LambdaNode> mdef){
        super(instr,mdef);
    }

    public static List<Invoke<?>> inspectCall(LambdaDump dump){
        if( dump==null )throw new IllegalArgumentException( "dump==null" );
        List<Invoke<?>> result = new ArrayList<>();
        inspectCall(result,dump);
        return result;
    }
    private static void inspectCall(List<Invoke<?>> result, LambdaDump dump){
        if( result==null )throw new IllegalArgumentException( "result==null" );
        if( dump==null )throw new IllegalArgumentException( "dump==null" );

        var lnode = dump.getLambdaNode();
        if( lnode==null )throw new IllegalArgumentException("dump.getLambdaNode()==null");

        lnode.walk().tree().forEach( t -> {
            var node = t.getNode();
            var meth = node.getMethod();
            var byteCodes = meth!=null ? meth.getMethodByteCodes() : null;
            if( byteCodes!=null ){
                for( var bc : byteCodes ){
                    if( bc==null )continue;
                    if( bc instanceof MInvokeDynamicInsn ){
                        result.add(new InvokeDynamicCall(
                            (MInvokeDynamicInsn) bc,
                            Tuple2.of(dump,node)
                        ));
                    }else if( bc instanceof MMethodInsn ){
                        result.add(
                            new InvokeMethod(
                                (MMethodInsn) bc,
                                Tuple2.of(dump,node)
                            )
                        );
                    }
                }
            }
        });
    }

    public abstract String getOwner();
    public abstract String getMethodName();
    public abstract MethodDescTypes getMethodTypes();
}
