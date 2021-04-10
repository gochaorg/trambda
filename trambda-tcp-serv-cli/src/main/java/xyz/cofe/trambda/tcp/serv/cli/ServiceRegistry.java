package xyz.cofe.trambda.tcp.serv.cli;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ServiceRegistry {
    public final ServerBuildInfo<?> buildInfo;

    public ServiceRegistry(ServerBuildInfo<?> bi){
        if( bi==null )throw new IllegalArgumentException( "bi==null" );
        this.buildInfo = bi;
    }

    public void daemon( boolean d ){
        buildInfo.setDaemon(d);
    }

    public void security(@DelegatesTo(SecurityConfiguration.class) Closure<?> sconf){
        if( sconf==null )throw new IllegalArgumentException( "sconf==null" );
        var s= new SecurityConfiguration();
        sconf.setDelegate( s );
        sconf.setResolveStrategy( Closure.DELEGATE_FIRST );
        sconf.call(s);
        buildInfo.setSecurFilter( s.createFilter() );
    }
}
