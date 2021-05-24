package xyz.cofe.jasm.ast;

import java.util.function.Consumer;
import xyz.cofe.text.tparse.TPointer;

public abstract class ASTBase<SELF extends ASTBase<SELF>> implements AST {
    protected ASTBase(){}
    protected ASTBase(ASTBase<SELF> sample){
        if( sample!=null ){
            this.begin = sample.begin;
            this.end = sample.end;
        }
    }
    public ASTBase(TPointer begin, TPointer end){
        if( begin==null )throw new IllegalArgumentException("begin==null");
        if( end==null )throw new IllegalArgumentException("end==null");
        this.begin = begin;
        this.end = end;
    }

    //public ASTBase clone(){ return new ASTBase(this); }
    public abstract SELF clone();
    protected SELF cloneAndConf(Consumer<SELF> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        SELF c = clone();
        conf.accept(c);
        return c;
    }

    protected TPointer begin;
    @Override public TPointer begin() {
        return begin;
    }
    public SELF begin(TPointer ptr){
        if( ptr==null )throw new IllegalArgumentException( "ptr==null" );
        begin = ptr;
        //noinspection unchecked
        return (SELF) this;
    }

    protected TPointer end;
    @Override public TPointer end() {
        return end;
    }
    public SELF end(TPointer ptr){
        if( ptr==null )throw new IllegalArgumentException( "ptr==null" );
        this.end = ptr;
        //noinspection unchecked
        return (SELF) this;
    }
}