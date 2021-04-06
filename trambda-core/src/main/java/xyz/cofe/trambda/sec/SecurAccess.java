package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.trambda.bc.MethodDef;

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

    public static List<SecurAccess<?, MethodDef>> inspect( MethodDef mdef ){
        if( mdef==null )throw new IllegalArgumentException( "mdef==null" );
        ArrayList<SecurAccess<?, MethodDef>> res = new ArrayList<>();
        res.addAll( FieldAccess.inspectField(mdef) );
        res.addAll( Call.inspectCall(mdef) );
        return res;
    }
}
