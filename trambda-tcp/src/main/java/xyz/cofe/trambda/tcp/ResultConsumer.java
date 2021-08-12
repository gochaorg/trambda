package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.log.api.Logger;

public class ResultConsumer<Req extends Message, Res extends Message> {
    private static final Logger log = Logger.of(ResultConsumer.class);

    private final TcpProtocol proto;
    private final Req req;
    private final Map<Integer,Consumer<ErrMessage>> errorConsumers;
    private final Map<Integer,Consumer<Res>> responseConsumers;

    public ResultConsumer(TcpProtocol proto, Req req, Map<Integer,Consumer<ErrMessage>> errorConsumers, Map<Integer,Consumer<Res>> responseConsumers){
        if( req == null ) throw new IllegalArgumentException("req==null");
        if( proto == null )throw new IllegalArgumentException( "proto==null" );
        if( errorConsumers == null )throw new IllegalArgumentException( "errorConsumers == null" );
        if( responseConsumers==null )throw new IllegalArgumentException( "responseConsumers==null" );

        this.proto = proto;
        this.req = req;
        this.errorConsumers = errorConsumers;
        this.responseConsumers = responseConsumers;
    }

    private volatile Consumer<Res> consumer;

    public ResultConsumer<Req, Res> onSuccess(Consumer<Res> response){
        return onSuccess(response, null);
    }

    public ResultConsumer<Req, Res> onSuccess(Consumer<Res> response, Consumer<Tuple2<Consumer<Res>,Consumer<Res>>> changes){
        if( response == null ) throw new IllegalArgumentException("response==null");
        if( consumer!=null ){
            @SuppressWarnings("rawtypes") Consumer eCons = consumer;
            consumer = msg -> {
                //noinspection unchecked
                eCons.accept(msg);
                //noinspection unchecked,rawtypes,rawtypes
                ((Consumer)response).accept(msg);
            };
            if( changes!=null ){
                //noinspection unchecked
                changes.accept(Tuple2.of(eCons, consumer));
            }
        }else {
            consumer = response;
            if( changes!=null ){
                changes.accept(Tuple2.of(null, consumer));
            }
        }
        return this;
    }

    private volatile Consumer<ErrMessage> errConsumer;

    public ResultConsumer<Req, Res> onFail(Consumer<ErrMessage> response){
        return onFail(response, null);
    }

    public ResultConsumer<Req, Res> onFail(Consumer<ErrMessage> response, Consumer<Tuple2<Consumer<ErrMessage>,Consumer<ErrMessage>>> changes){
        if( response == null ) throw new IllegalArgumentException("response==null");
        if( errConsumer!=null ){
            var eCons = errConsumer;
            errConsumer = msg -> {
                eCons.accept(msg);
                response.accept(msg);
            };
            if( changes!=null ){
                changes.accept( Tuple2.of(eCons, errConsumer) );
            }
        }else {
            errConsumer = response;
            if( changes!=null ){
                changes.accept( Tuple2.of(null, errConsumer) );
            }
        }
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
        log.info("fetch()");

        AtomicReference<Res> res = new AtomicReference<>(null);
        AtomicReference<ErrMessage> err = new AtomicReference<>(null);
        final Object sync = ResultConsumer.this;

        try{
            log.debug("synchronized( sync )");
            synchronized( sync ){
                log.debug("proto.send");

                proto.send(req, msgId -> {
                    log.debug("consumer message.id {}",msgId);

                    Consumer<Res> succ = msg -> {
                        log.debug("succ consumer synchronized( sync )");
                        synchronized( sync ){
                            log.debug("succ consumer synchronized( sync ) - enter ok");
                            //noinspection unchecked
                            res.set((Res) msg);
                            sync.notifyAll();
                        }
                    };

                    AtomicReference<Consumer<Res>> succCons = new AtomicReference<>();

                    log.debug("onSuccess");
                    onSuccess(succ, p -> succCons.set(p.b()));

                    log.debug("responseConsumers.put");
                    responseConsumers.put(msgId, succCons.get());

                    Consumer<ErrMessage> fail =  err0 -> {
                        log.debug("fail consumer synchronized( sync )");
                        synchronized( sync ){
                            log.debug("fail consumer synchronized( sync ) - enter ok");
                            err.set(err0);
                            sync.notifyAll();
                        }
                    };

                    AtomicReference<Consumer<ErrMessage>> failCons = new AtomicReference<>();

                    log.debug("onFail");
                    onFail(fail, p -> failCons.set(p.b()));

                    log.debug("errorConsumers.put");
                    errorConsumers.put(msgId, failCons.get());
                });

                try{
                    log.debug("sync.wait");
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
