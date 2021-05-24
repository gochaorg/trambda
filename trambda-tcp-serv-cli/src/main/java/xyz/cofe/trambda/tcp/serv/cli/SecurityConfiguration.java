package xyz.cofe.trambda.tcp.serv.cli;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.sec.SecurityFilter;
import xyz.cofe.trambda.sec.SecurityFilters;

public class SecurityConfiguration {
    private SecurityFilters.Builder<String, Tuple2<LambdaDump, LambdaNode>> sfBuilder;

    public SecurityConfiguration(){
        sfBuilder = SecurityFilters.create();
    }

    public void allow(@DelegatesTo(SecurityPredicateBuilder.class) Closure<?> f){
        if( f==null )throw new IllegalArgumentException( "f==null" );
        var spb = new SecurityPredicateBuilder<>(sfBuilder.allow());
        f.setDelegate(spb);
        f.setResolveStrategy(Closure.DELEGATE_FIRST);
        f.call(spb);
    }

    public void deny(@DelegatesTo(SecurityPredicateBuilder.class) Closure<?> f){
        if( f==null )throw new IllegalArgumentException( "f==null" );
        var spb = new SecurityPredicateBuilder<>(sfBuilder.deny());
        f.setDelegate(spb);
        f.setResolveStrategy(Closure.DELEGATE_FIRST);
        f.call(spb);
    }

    public SecurityFilter<String, Tuple2<LambdaDump, LambdaNode>> createFilter(){
        return sfBuilder.build();
    }
}
