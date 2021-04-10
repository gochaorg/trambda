package xyz.cofe.trambda.tcp.serv.cli;

import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.function.Consumer;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.SecurityFilter;
import xyz.cofe.trambda.tcp.TcpServer;

public class ServerBuildInfo<A> {
    public ServerBuildInfo<A> configure(Consumer<ServerBuildInfo<A>> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region daemon : boolean
    private boolean daemon = false;

    public boolean isDaemon(){
        return daemon;
    }

    public void setDaemon(boolean daemon){
        this.daemon = daemon;
    }
    //endregion
    //region securFilter : SecurFilter<String, MethodDef>
    private SecurityFilter<String, MethodDef> securityFilter;

    public SecurityFilter<String, MethodDef> getSecurFilter(){
        return securityFilter;
    }

    public void setSecurFilter(SecurityFilter<String, MethodDef> securityFilter){
        this.securityFilter = securityFilter;
    }
    //endregion

    public ServerBuildInfo<A> service( Class<A> serviceClass ){
        if( serviceClass==null )throw new IllegalArgumentException( "serviceClass==null" );
        try{
            service = serviceClass.getConstructor().newInstance();
            return this;
        } catch( InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
            throw new Error("can't create service from "+serviceClass, e);
        }
    }
    public ServerBuildInfo<A> service( Class<A> serviceClass, Class<? extends A> implClass ){
        if( serviceClass==null )throw new IllegalArgumentException( "serviceClass==null" );
        if( implClass==null )throw new IllegalArgumentException( "implClass==null" );
        try{
            service = implClass.getConstructor().newInstance();
            return this;
        } catch( InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
            throw new Error("can't create service from "+serviceClass, e);
        }
    }

    public ServerBuildInfo<A> service(A service){
        this.service = service;
        return this;
    }

    private A service;
    public A service(){
        return service;
    }

    public TcpServer<A> build(ServerSocket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        var srvc = service();
        if( srvc==null )throw new IllegalStateException("service is null");

        TcpServer<A> serv = new TcpServer<A>(socket, ses -> srvc, securityFilter);
        serv.setDaemon(isDaemon());

        return serv;
    }
}
