package xyz.cofe.jasm.ast;

import java.util.Optional;
import xyz.cofe.text.tparse.GR;
import xyz.cofe.text.tparse.TPointer;

public class ProxyGR<T extends AST> implements GR<TPointer,T> {
    public ProxyGR(GR<TPointer,T> target){
        //if( target==null )throw new IllegalArgumentException( "target==null" );
        this.target = target;
    }

    protected GR<TPointer,T> target;

    public GR<TPointer,T> target(){
        return target;
    }

    public ProxyGR<T> target(GR<TPointer,T> target){
        if( target==null )throw new IllegalArgumentException( "target==null" );
        this.target = target;
        return this;
    }

    @Override
    public Optional<T> apply(TPointer tPointer){
        if( target==null )throw new IllegalStateException("target == null");
        return target.apply(tPointer);
    }
}
