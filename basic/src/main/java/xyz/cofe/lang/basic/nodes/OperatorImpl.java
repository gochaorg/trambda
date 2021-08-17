package xyz.cofe.lang.basic.nodes;

import scala.Function1;
import scala.collection.Seq;

public class OperatorImpl implements Function1<Seq<Object>,Object> {
    public final String name;
    public OperatorImpl(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        this.name = name;
    }

    @Override
    public Object apply(Seq<Object> v1) {
        return null;
    }

    @Override
    public <A> Function1<A, Object> compose(Function1<A, Seq<Object>> g) {
        return x -> apply(g.apply(x));
    }

    @Override
    public <A> Function1<Seq<Object>, A> andThen(Function1<Object, A> g) {
        return x -> g.apply(apply(x));
    }
}
