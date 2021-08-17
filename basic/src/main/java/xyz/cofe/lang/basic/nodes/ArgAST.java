package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class ArgAST extends TAST<BasicParser.ArgContext, AST<?, ?>> {
    public ArgAST(BasicParser.ArgContext antlrRule) {
        super(antlrRule);
    }

    public String getName(){ return antlrRule.name.getText(); }
    public String getTypeName(){ return antlrRule.type.getText(); }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of();
    }

    @Override
    public String toString() {
        return ArgsAST.class.getSimpleName()+" "+getName()+" : "+getTypeName()+" -> "+getType();
    }
}
