package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class VarRefAST extends TAST<BasicParser.VarRefContext, AST<?, ?>> {
    public VarRefAST(BasicParser.VarRefContext antlrRule) {
        super(antlrRule);
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of();
    }

    public String getName(){ return antlrRule.ID().getText(); }

    protected AST<?,?> definition;
    public AST<?,?> getDefinition(){ return definition; }
    public void setDefinition(AST<?,?> ast){
        this.definition = ast;
    }

    @Override
    public String toString() {
        return VarRefAST.class.getSimpleName()+" "+getName()+" -> "+getType()+(
            definition!=null ? " defined in "+definition : ""
        );
    }
}
