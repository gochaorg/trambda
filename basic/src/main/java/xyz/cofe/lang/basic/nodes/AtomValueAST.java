package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class AtomValueAST extends TAST<BasicParser.AtomValueContext, AST<?, ?>> {
    public AtomValueAST(BasicParser.AtomValueContext antlrRule) {
        super(antlrRule);
    }

    protected AST<?,?> expr;
    public AST<?,?> getExpr(){
        if( expr !=null )return expr;
        expr = wrap(antlrRule.atom());
        return expr;
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of(getExpr());
    }

    @Override
    public String toString() {
        return AtomValueAST.class.getSimpleName()+" -> "+getType();
    }
}
