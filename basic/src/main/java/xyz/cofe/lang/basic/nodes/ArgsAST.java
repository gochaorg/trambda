package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;
import java.util.stream.Collectors;

public class ArgsAST extends AST<BasicParser.ArgsContext, ArgAST> {
    public ArgsAST(BasicParser.ArgsContext antlrRule) {
        super(antlrRule);
    }

    @Override
    protected List<ArgAST> createChildren() {
        return antlrRule.arg().stream().map(AST::wrap).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return ArgsAST.class.getSimpleName();
    }
}
