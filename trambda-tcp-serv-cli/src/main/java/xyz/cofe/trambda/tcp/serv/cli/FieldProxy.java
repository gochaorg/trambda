package xyz.cofe.trambda.tcp.serv.cli;

import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.mth.MFieldInsn;
import xyz.cofe.trambda.sec.FieldAccess;
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.TypeDesc;

public class FieldProxy {
    public final FieldAccess fieldAccess;
    public FieldProxy(FieldAccess fieldAccess){
        if( fieldAccess==null )throw new IllegalArgumentException( "fieldAccess==null" );
        this.fieldAccess = fieldAccess;
    }

    public String getFieldOwner(){
        return fieldAccess.getOwner();
    }

    public String getFieldName(){
        return fieldAccess.getFieldName();
    }

    public TypeDesc getFieldType(){
        return fieldAccess.getFieldType();
    }

    public FieldAccess.Operation getOperation(){
        return fieldAccess.getOperation();
    }

    public boolean isStatic(){
        return fieldAccess.isStatic();
    }

    public boolean isField(){
        return fieldAccess.isField();
    }

    public boolean isReadAccess(){
        return fieldAccess.isReadAccess();
    }

    public boolean isWriteAccess(){
        return fieldAccess.isWriteAccess();
    }

    public Tuple2<LambdaDump, LambdaNode> getScope(){
        return fieldAccess.getScope();
    }

    public MFieldInsn getInstruction(){
        return fieldAccess.getInstruction();
    }

    public static List<SecurAccess<?, Tuple2<LambdaDump, LambdaNode>>> inspect(LambdaDump mdef){
        return SecurAccess.inspect(mdef);
    }
}
