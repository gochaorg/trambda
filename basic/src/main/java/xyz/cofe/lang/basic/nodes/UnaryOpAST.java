package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class UnaryOpAST extends TAST<BasicParser.UnaryOpContext, AST<?, ?>> {
    public UnaryOpAST(BasicParser.UnaryOpContext antlrRule) {
        super(antlrRule);
    }

    protected AST<?,?> expr;
    public AST<?,?> getExpr(){
        if( expr !=null )return expr;
        expr = wrap(antlrRule.expr());
        return expr;
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of(getExpr());
    }

    //region operator : String - Имя оператора
    public String getOperator(){
        return antlrRule.op.getText();
    }
    //endregion
    //region operatorImpl : OperatorImpl - реализация оператора
    protected OperatorImpl operatorImpl;

    public OperatorImpl getOperatorImpl() {
        return operatorImpl;
    }

    public void setOperatorImpl( OperatorImpl operatorImpl ) {
        this.operatorImpl = operatorImpl;
    }
    //endregion

    @Override
    public String toString() {
        return UnaryOpAST.class.getSimpleName()+" op="+getOperator()+" -> "+getType();
    }
}
