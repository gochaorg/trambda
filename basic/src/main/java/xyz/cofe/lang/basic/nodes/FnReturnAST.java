package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class FnReturnAST extends TAST<BasicParser.FnReturnContext, AST<?, ?>> {
    public FnReturnAST(BasicParser.FnReturnContext antlrRule) {
        super(antlrRule);
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of();
    }

    public String getTypeName(){ return antlrRule.type.getText(); }

    @Override
    public String toString() {
        return FnReturnAST.class.getSimpleName()+" : "+getTypeName()+" -> "+getType();
    }
}
