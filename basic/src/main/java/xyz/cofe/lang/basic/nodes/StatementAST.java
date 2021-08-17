package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class StatementAST extends AST<BasicParser.StatementContext, AST<?, ?>> {
    public StatementAST(BasicParser.StatementContext antlrRule) {
        super(antlrRule);
    }

    protected ReturnAST returnAST;
    public ReturnAST getReturn(){
        if( returnAST!=null )return returnAST;
        returnAST = wrap(antlrRule.returnStatement());
        return returnAST;
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of(getReturn());
    }

    @Override
    public String toString() {
        return StatementAST.class.getSimpleName();
    }
}
