package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class ReturnAST extends TAST<BasicParser.ReturnStatementContext, AST<?, ?>> {
    public ReturnAST(BasicParser.ReturnStatementContext antlrRule) {
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

    @Override
    public String toString() {
        return ReturnAST.class.getSimpleName()+" -> "+getType();
    }
}
