package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ResultConsumer<Req extends Message, Res extends Message> {
    private final TcpProtocol proto;
    private final Req req;
    private final Map<Integer,Consumer<ErrMessage>> errorConsumers;
    private final Map<Integer,Consumer<? extends Message>> responseConsumers;

    public ResultConsumer(TcpProtocol proto, Req req, Map<Integer,Consumer<ErrMessage>> errorConsumers, Map<Integer,Consumer<? extends Message>> responseConsumers){
        if( req == null ) throw new IllegalArgumentException("req==null");
        if( proto == null )throw new IllegalArgumentException( "proto==null" );
        if( errorConsumers == null )throw new IllegalArgumentException( "errorConsumers == null" );
        if( responseConsumers==null )throw new IllegalArgumentException( "responseConsumers==null" );

        this.proto = proto;
        this.req = req;
        this.errorConsumers = errorConsumers;
        this.responseConsumers = responseConsumers;
    }

    private volatile Consumer<? extends Message> consumer;

    public ResultConsumer<Req, Res> onSuccess(Consumer<Res> response){
        if( response == null ) throw new IllegalArgumentException("response==null");
        consumer = response;
        return this;
    }

    private volatile Consumer<ErrMessage> errConsumer;

    public ResultConsumer<Req, Res> onFail(Consumer<ErrMessage> response){
        if( response == null ) throw new IllegalArgumentException("response==null");
        errConsumer = response;
        return this;
    }

    public ResultConsumer<Req, Res> configure(Consumer<Req> reqConf){
        if( reqConf == null ) throw new IllegalArgumentException("reqConf==null");
        reqConf.accept(req);
        return this;
    }

    public void send(){
        try{
            proto.send(req, msgId -> {
                if( consumer != null ) responseConsumers.put(msgId, consumer);
                if( errConsumer != null ) errorConsumers.put(msgId, errConsumer);
            });
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    public Res fetch(){
        AtomicReference<Res> res = new AtomicReference<>(null);
        AtomicReference<ErrMessage> err = new AtomicReference<>(null);
        final Object sync = ResultConsumer.this;

        try{
            synchronized( sync ){
                proto.send(req, msgId -> {
                    responseConsumers.put(msgId, msg -> {
                        synchronized( sync ){
                            //noinspection unchecked
                            res.set((Res) msg);
                            sync.notifyAll();
                        }
                    });
                    errorConsumers.put(msgId, err0 -> {
                        synchronized( sync ){
                            err.set(err0);
                            sync.notifyAll();
                        }
                    });
                });
                try{
                    sync.wait();
                } catch( InterruptedException e ) {
                    throw new IOError(e);
                }
            }
        } catch( IOException e ) {
            throw new IOError(e);
        }

        var errMsg = err.get();
        if( errMsg != null ){
            throw new RuntimeException(errMsg.getMessage());
        }

        return res.get();
    }
}
