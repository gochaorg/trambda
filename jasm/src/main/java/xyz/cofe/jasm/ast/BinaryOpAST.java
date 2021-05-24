package xyz.cofe.jasm.ast;

import xyz.cofe.text.tparse.Pointer;

public class BinaryOpAST extends ASTBase<BinaryOpAST> {
    protected BinaryOpAST(BinaryOpAST sample) {
        super(sample);
        if( sample!=null ){
            operator = sample.operator;
            left = sample.left;
            right = sample.right;
        }
    }

    public BinaryOpAST(AST left, CTokenAST op, AST right) {
        if( op==null )throw new IllegalArgumentException("op==null");
        if( left==null )throw new IllegalArgumentException("left==null");
        if( right==null )throw new IllegalArgumentException("right==null");
        this.begin = Pointer.min( left.begin(), left.end(), right.begin(), right.end() );
        this.end = Pointer.max(left.begin(), left.end(), right.begin(), right.end());
        this.operator = op;
        this.left = left;
        this.right = right;
    }

    public BinaryOpAST clone(){ return new BinaryOpAST(this); }

    protected CTokenAST operator;
    public CTokenAST operator(){ return operator; }
    public BinaryOpAST operator(CTokenAST op ){
        if( op==null )throw new IllegalArgumentException("op==null");
        BinaryOpAST c = clone();
        c.operator = op;
        return c;
    }

    protected AST left;
    public AST left(){ return left; }

    protected AST right;
    public AST right(){ return right; }

    @Override
    public String toString() {
        return
            "left("+left+")"+
                " "+BinaryOpAST.class.getSimpleName()+" "+operator.token().text()+
                " right("+right+")";
    }
}