package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class LiteralAST extends TAST<BasicParser.LiteralContext, AST<?, ?>> {
    public LiteralAST(BasicParser.LiteralContext antlrRule) {
        super(antlrRule);
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of();
    }

    @Override
    public String toString() {
        return LiteralAST.class.getSimpleName()+" -> "+getType();
    }
}
