package xyz.cofe.trambda.tcp;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.LambdaDump;

public class TcpQuery<ENV> extends AsmQuery<ENV> implements AutoCloseable {
    protected final TcpClient client;
    public TcpClient getClient(){ return client; }

    public TcpQuery(TcpClient client){
        if( client==null )throw new IllegalArgumentException( "client==null" );
        this.client = client;
    }

    @Override
    public void close() throws Exception {
        client.close();
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
    protected final Map<Fn1<?,?>,CompileResult> fnKeys = new ConcurrentHashMap<>();

    @Override
    protected <RES> RES call(Fn1<ENV, RES> fn, SerializedLambda sl, LambdaDump mdef){
        var key = fnKeys.get(fn);
        if( key!=null ){
            return call(key,sl);
        }else{
            var ckey = client.compile(mdef).fetch();
            fnKeys.put(fn,ckey);
            return call(ckey,sl);
        }
    }
    protected <RES> RES call(CompileResult key,SerializedLambda sl){
        var execRes = client.execute(key).configure( exec -> {
            if( sl.getCapturedArgCount()>0 ){
                var args = new ArrayList<Object>();
                exec.setCapturedArgs(args);
                for( int ai=0;ai<sl.getCapturedArgCount();ai++ ){
                    args.add(sl.getCapturedArg(ai));
                }
            }else{
                exec.setCapturedArgs(null);
            }
        }).fetch();
        return (RES)execRes.getValue();
    }
}
