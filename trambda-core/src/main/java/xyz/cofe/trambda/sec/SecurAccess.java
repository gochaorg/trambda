package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;

public abstract class SecurAccess<INSTR, SCOPE> {
    protected final SCOPE scope;
    protected final INSTR instruction;

    public SecurAccess(INSTR instr, SCOPE scope){
        if( scope==null )throw new IllegalArgumentException( "scope==null" );
        if( instr==null )throw new IllegalArgumentException( "instr==null" );
        this.scope = scope;
        this.instruction = instr;
    }

    public abstract SecurAccess<INSTR, SCOPE> clone();

    public SCOPE getScope(){ return scope; }
    public INSTR getInstruction(){ return instruction; }

    public static List<SecurAccess<?, Tuple2<LambdaDump, LambdaNode>>> inspect(LambdaDump dump){
        if( dump==null )throw new IllegalArgumentException( "dump==null" );
        ArrayList<SecurAccess<?, Tuple2<LambdaDump, LambdaNode>>> res = new ArrayList<>();
        res.addAll( FieldAccess.inspectField(dump) );
        res.addAll( Invoke.inspectCall(dump) );
        return res;
    }
}
