package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class AtomAST extends TAST<BasicParser.AtomContext, AST<?, ?>> {
    public AtomAST(BasicParser.AtomContext antlrRule) {
        super(antlrRule);
    }

    protected AST<?,?> expr;
    public AST<?,?> getExpr(){
        if( expr !=null )return expr;
        expr = wrap(
            antlrRule.varRef() != null ?
                antlrRule.varRef() :
                antlrRule.literal()
        );
        return expr;
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of(getExpr());
    }

    @Override
    public String toString() {
        return AtomAST.class.getSimpleName()+" -> "+getType();
    }
}
