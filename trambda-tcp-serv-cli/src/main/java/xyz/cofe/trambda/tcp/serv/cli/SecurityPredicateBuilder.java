package xyz.cofe.trambda.tcp.serv.cli;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.SecurityFilters;

public class SecurityPredicateBuilder<SELF extends SecurityFilters.PredicateBuilder<String ,MethodDef,SELF>> {
    private final SecurityFilters.PredicateBuilder<String,MethodDef,SELF> sfpb;

    public SecurityPredicateBuilder(SecurityFilters.PredicateBuilder<String, MethodDef,SELF> sfpb){
        this.sfpb = sfpb;
    }

    public void any(String message){
        if( message==null )throw new IllegalArgumentException( "message==null" );
        sfpb.any(message);
    }

    public void invoke(String message, @DelegatesTo(CallProxy.class) Closure<?> f){
        if( message==null )throw new IllegalArgumentException( "message==null" );
        if( f==null )throw new IllegalArgumentException( "f==null" );

        sfpb.invoke(message, c -> {
            CallProxy cp = new CallProxy(c);
            f.setDelegate(cp);
            f.setResolveStrategy(Closure.DELEGATE_FIRST);
            Object o = f.call(cp);
            if( o instanceof Boolean ){
                return (Boolean)o;
            }
            throw new Error("method predicate ("+message+") execution error - return not boolean: "+(o!=null ? o.getClass() : "null"));
        });
    }

    public void field(String message, @DelegatesTo(FieldProxy.class) Closure<?> f){
        if( message==null )throw new IllegalArgumentException( "message==null" );
        if( f==null )throw new IllegalArgumentException( "f==null" );

        sfpb.field(message, c -> {
            FieldProxy fp = new FieldProxy(c);
            f.setDelegate(fp);
            f.setResolveStrategy(Closure.DELEGATE_FIRST);
            Object o = f.call(fp);
            if( o instanceof Boolean ){
                return (Boolean)o;
            }
            throw new Error("field predicate ("+message+") execution error - return not boolean: "+(o!=null ? o.getClass() : "null"));
        });
    }
}
