package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.Fn;
import xyz.cofe.trambda.bc.MethodDef;

public class TcpQuery<ENV> extends AsmQuery<ENV> {
    protected final TcpClient client;

    public TcpQuery(TcpClient client){
        if( client==null )throw new IllegalArgumentException( "client==null" );
        this.client = client;
    }

    public static <ENV> Builder<ENV> create(Class<ENV> c){
        return new Builder<>();
    }

    public static class Builder<ENV> {
        protected String host = "localhost";
        public Builder<ENV> host(String address){
            this.host = address;
            return this;
        }

        protected int port;
        public Builder<ENV> port(int port){
            this.port = port;
            return this;
        }

        public TcpQuery<ENV> build(){
            if( host==null )throw new IllegalStateException("host==nulll");
            try{
                Socket socket = new Socket(host,port);
                return new TcpQuery<ENV>( new TcpClient(socket) );
            } catch( IOException e ) {
                throw new IOError(e);
            }
        }
    }

    protected final Map<Fn<?,?>,CompileResult> fnKeys = new ConcurrentHashMap<>();

    @Override
    protected <RES> RES call(Fn<ENV, RES> fn, SerializedLambda sl, MethodDef mdef){
        var key = fnKeys.get(fn);
        if( key!=null ){
            return call(key);
        }else{
            var ckey = client.compile(mdef).fetch();
            fnKeys.put(fn,ckey);
            return call(ckey);
        }
    }

    protected <RES> RES call(CompileResult key){
        var execRes = client.execute(key).fetch();
        return (RES)execRes.getValue();
    }
}
