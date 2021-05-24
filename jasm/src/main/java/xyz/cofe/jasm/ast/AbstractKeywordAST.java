package xyz.cofe.jasm.ast;

import xyz.cofe.text.tparse.TPointer;

public class AbstractKeywordAST<T> extends ASTBase<AbstractKeywordAST<T>> {
    public AbstractKeywordAST(AbstractKeywordAST<T> sample) {
        super(sample);
        if( sample!=null ){
            if( sample.token instanceof ASTBase ){
                this.token = (T)((ASTBase<?>)sample.token).clone();
            }else {
                this.token = sample.token;
            }
        }
    }

    public AbstractKeywordAST(TPointer begin, T token) {
        if(begin==null)throw new IllegalArgumentException("begin==null");
        if( token ==null )throw new IllegalArgumentException("keywordTok==null");
        this.begin = begin;
        this.end = begin.move(1);
        this.token = token;
    }

    public AbstractKeywordAST<T> clone(){ return new AbstractKeywordAST<>(this); }

    protected T token;
    public T token(){ return token; }
    public AbstractKeywordAST<T> token(T t ){
        if( t==null )throw new IllegalArgumentException("t==null");
        AbstractKeywordAST<T> c = clone();
        c.token = t;
        return c;
    }
}
