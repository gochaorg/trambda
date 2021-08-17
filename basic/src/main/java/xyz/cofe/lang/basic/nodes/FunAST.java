package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Определение функции
 */
public class FunAST extends AST<BasicParser.FunctionContext, AST<?, ?>> {
    public FunAST(BasicParser.FunctionContext antlrRule) {
        super(antlrRule);
    }

    /**
     * Возвращает имя функции
     * @return имя функции
     */
    public String getName() {
        return antlrRule.name.getText();
    }

    protected ArgsAST args;
    public ArgsAST getArgs(){
        if( args !=null )return args;
        args = wrap(antlrRule.args());
        return args;
    }

    protected FnReturnAST returns;
    public FnReturnAST getReturns(){
        if( returns !=null )return returns;
        returns = wrap(antlrRule.fnReturn());
        return returns;
    }

    protected List<StatementAST> statements;
    public List<StatementAST> getStatements(){
        if( statements !=null )return statements;
        statements = antlrRule.statement() != null ?
            antlrRule.statement().stream().map(AST::wrap).collect(Collectors.toList()) :
            new ArrayList<>();
        return statements;
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        ArrayList<AST<?,?>> lst = new ArrayList<>();
        lst.add(getArgs());
        lst.add(getReturns());
        lst.addAll(getStatements());
        return lst;
    }

    @Override
    public String toString() {
        return FunAST.class.getSimpleName()+" "+getName();
    }
}
