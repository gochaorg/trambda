package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.BasicParser;

import java.util.List;

public class BinOpAST extends TAST<BasicParser.BinOpContext, AST<?, ?>> {
    public BinOpAST(BasicParser.BinOpContext antlrRule) {
        super(antlrRule);
    }

    //region left - левый операнд
    protected AST<?,?> left;
    public AST<?,?> getLeft(){
        if( left !=null )return left;
        left = wrap(antlrRule.left);
        return left;
    }
    //endregion
    //region right - правый операнд
    protected AST<?,?> right;
    public AST<?,?> getRight(){
        if( right !=null )return right;
        right = wrap(antlrRule.right);
        return right;
    }
    //endregion
    //region operator : String - Имя оператора
    public String getOperator(){
        return antlrRule.op.getText();
    }
    //endregion
    //region operatorImpl : OperatorImpl - реализация оператора
    protected OperatorImpl operatorImpl;

    public OperatorImpl getOperatorImpl() {
        return operatorImpl;
    }

    public void setOperatorImpl( OperatorImpl operatorImpl ) {
        this.operatorImpl = operatorImpl;
    }
    //endregion

    @Override
    protected List<AST<?, ?>> createChildren() {
        return List.of(getLeft(), getRight());
    }

    @Override
    public String toString() {
        return BinOpAST.class.getSimpleName()+" op="+getOperator()+" -> "+getType();
    }
}
