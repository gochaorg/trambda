package xyz.cofe.trambda.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import xyz.cofe.fn.Tuple;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.log.api.Logger;

import static xyz.cofe.trambda.tcp.TcpHeader.encode;

/**
 * Поддержка TCP протокола
 */
public class TcpProtocol {
    /**
     * Конструктор
     * @param socket сокет
     */
    public TcpProtocol(Socket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;

        getOutput = null;
        getInput = null;
        var l = Logger.of(TcpProtocol.class);
        getLogger = ()->l;
    }

    /**
     * Конструктор
     * @param socket сокет
     * @param logger логгер
     */
    public TcpProtocol(Socket socket, Logger logger){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;

        getOutput = null;
        getInput = null;
        getLogger = ()->logger;
    }

    /**
     * Конструктор
     * @param outputStream исходящий поток данных
     * @param inputStream входящий поток данных
     * @param logger логгер
     */
    public TcpProtocol(OutputStream outputStream, InputStream inputStream, Logger logger){
        if( outputStream==null )throw new IllegalArgumentException( "outputStream==null" );
        if( inputStream==null )throw new IllegalArgumentException( "inputStream==null" );
        if( logger==null )throw new IllegalArgumentException( "logger==null" );

        getOutput = ()->outputStream;
        getInput = ()->inputStream;
        getLogger = ()->logger;
        socket = null;
    }

    private final Socket socket;
    private final Supplier<OutputStream> getOutput;
    private final Supplier<InputStream> getInput;
    private final Supplier<Logger> getLogger;
    private final AtomicInteger sid = new AtomicInteger(0);

    /**
     * Возвращает исходящий поток данных
     * @return исходящий поток данных
     * @throws IOException ошибка сети
     */
    private OutputStream output() throws IOException {
        if( socket!=null )return socket.getOutputStream();
        if( getOutput==null )throw new IllegalStateException("getOutput==null");
        return getOutput.get();
    }

    /**
     * Возвращает входящий поток данных
     * @return входящий поток данных
     * @throws IOException ошибка сети
     */
    private InputStream intput() throws  IOException {
        if( socket!=null )return socket.getInputStream();
        if( getInput==null )throw new IllegalStateException("getInput==null");
        return getInput.get();
    }

    /**
     * Возвращает логгер
     * @return логгер
     */
    private Logger log() {
        return getLogger.get();
    }

    //region send()
    /**
     * Отправка сообщения
     * @param method метод
     * @param payload полезная нагрузка
     * @param sendId идентификатор сообщения
     * @param headerValues дополнительные заголовки сообщения
     * @return идентификатор сообщения
     * @throws IOException ошибка сети
     */
    @SuppressWarnings("TypeParameterExplicitlyExtendsObject")
    @SafeVarargs
    public final int sendRaw(String method, byte[] payload, Consumer<Integer> sendId, HeaderValue<? extends Object>... headerValues) throws IOException {
        log().debug("sendRaw {} payload length {}",
            method,
            payload!=null ? payload.length : 0
        );

        int id = sid.incrementAndGet();
        if( sendId!=null ){
            sendId.accept(id);
        }

        // write header
        // noinspection rawtypes
        HeaderValue[] hvals = headerValues==null || headerValues.length<1 ?
            new HeaderValue[]{ TcpHeader.sid.create(id) } :
            new HeaderValue[ headerValues.length+1 ];

        if( headerValues!=null && headerValues.length>0 ){
            hvals[0] = TcpHeader.sid.create(id);
            System.arraycopy(headerValues,0,hvals,1,headerValues.length);
        }

        //noinspection unchecked
        byte[] header = encode(method, payload, hvals);

        log().trace("send header {} {}", header.length, Text.encodeHex(header));
        output().write(header);

        // write payload
        if( payload!=null && payload.length>0 ){
            log().trace("send payload {} {}", payload.length, Text.encodeHex(payload));
            output().write(payload);
        }

        output().flush();

        return id;
    }

    /**
     * Отправка сообщения
     * @param message сообщение
     * @param headerValues дополнительные заголовки сообщения
     * @return идентификатор сообщения
     * @throws IOException ошибка сети
     */
    @SuppressWarnings("TypeParameterExplicitlyExtendsObject")
    @SafeVarargs
    public final int send(Message message,HeaderValue<? extends Object> ... headerValues) throws IOException {
        if( message==null )throw new IllegalArgumentException( "message==null" );

        log().debug("send {}", message);
        return sendRaw(
            message.getClass().getSimpleName(),
            Serializer.toBytes(message),
            null,
            headerValues);
    }

    /**
     * Отправка сообщения
     * @param message сообщение
     * @param sid идентификатор сообщения
     * @param headerValues дополнительные заголовки сообщения
     * @return идентификатор сообщения
     * @throws IOException ошибка сети
     */
    @SuppressWarnings({"UnusedReturnValue","TypeParameterExplicitlyExtendsObject"})
    @SafeVarargs
    public final int send(Message message,Consumer<Integer> sid, HeaderValue<? extends Object> ... headerValues) throws IOException {
        if( message==null )throw new IllegalArgumentException( "message==null" );

        log().debug("send {}", message);
        return sendRaw(
            message.getClass().getSimpleName(),
            Serializer.toBytes(message),
            sid,
            headerValues);
    }
    //endregion
    //region process input
    /**
     * Получение сообщения из сети
     * @param prevStream предыдущая часть сообщения
     * @return сообщение или входящий поток закрыт
     * @throws IOException ошибка сети
     */
    public Optional<Tuple2<RawPack,Optional<BuffInputStream>>> receiveRaw(Optional<BuffInputStream> prevStream) throws IOException {
        if( prevStream==null )throw new IllegalArgumentException( "prevStream==null" );
        log().debug("receiveRaw()");

        int buffSize = 1024 * 8;
        var bufStrm = prevStream.orElse( new BuffInputStream(intput(),buffSize) );
        byte[] buff = new byte[buffSize];
        log().trace("buffer size {}",buffSize);

        while( true ){
            log().trace("mark {}",buffSize);
            bufStrm.mark(buffSize);

            int readed = -1;
            try{
                log().debug("read header, pos={} count={} markpos={}", bufStrm.pos(), bufStrm.count(), bufStrm.markpos());
                readed = bufStrm.read(buff);
                if( readed < 0 ){
                    log().info("stream closed");
                    return Optional.empty();
                }
            } catch( SocketException ex ) {
                if( ex.getMessage()!=null && ex.getMessage().matches("(?is).*socket\\s+closed.*")){
                    log().info("socket closed");
                    return Optional.empty();
                }else {
                    //log().error("read fail", ex);
                    throw ex;
                }
            }

            log().debug("readed {}, pos={} count={} markpos={}",readed, bufStrm.pos(), bufStrm.count(), bufStrm.markpos());

            var headerOpt = TcpHeader.parse(buff, 0, readed);
            if( headerOpt.isEmpty() ){
                log().debug("reset");
                bufStrm.reset();
                continue;
            }

            log().debug("header parsed");
            log().debug("buff data {} {}", readed, Text.encodeHex(Arrays.copyOf(buff,readed)));

            var header = headerOpt.get();
            log().debug("header size: {}",header.getHeaderSize());

            bufStrm.reset();
            log().debug("reseted, pos={} count={} markpos={}", bufStrm.pos(), bufStrm.count(), bufStrm.markpos());

            int skipSize = header.getHeaderSize();
            int skipped = 0;
            while( true ){
                int expectSkip = skipSize - skipped;

                log().debug("skip {}",expectSkip);
                long actualSkipped = bufStrm.skip(expectSkip);
                skipped += actualSkipped;
                if( skipped>=actualSkipped )break;
            }

            log().debug("pos={} count={} markpos={}", bufStrm.pos(), bufStrm.count(), bufStrm.markpos());
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            if( header.getPayloadSize() > 0 ){
                log().debug("payload size {}",header.getPayloadSize());
                while( true ){
                    int need = header.getPayloadSize() - ba.size();
                    if( need<=0 ){
                        log().debug("payload readed");
                        break;
                    }

                    log().debug("need read {}, pos={} count={} markpos={}",need,bufStrm.pos(), bufStrm.count(), bufStrm.markpos());
                    readed = bufStrm.read(buff,0,Math.min(need,buff.length));
                    if( readed<=0 ){
                        throw new IOException("broken message, input stream EOF");
                    }

                    log().trace("readed payload {}",
                        Text.encodeHex(Arrays.copyOf(buff,readed))
                    );

                    ba.write(buff,0,readed);
                }
            }

            RawPack rpack = new RawPack( header, ba.toByteArray() );

            log().debug("received h.size {} method {} h.payload {} d.payload {}",
                header.getHeaderSize(),
                header.getMethodName(),
                header.getPayloadSize(),
                rpack.getPayload().length
            );

            log().debug("bufStrm: pos={} count={} markpos={} available={}", bufStrm.pos(), bufStrm.count(), bufStrm.markpos(), bufStrm.available());

            if( bufStrm.available()>0 ){
                return Optional.of(Tuple2.of(rpack, Optional.of(bufStrm)));
            }else{
                return Optional.of(Tuple2.of(rpack, Optional.empty()));
            }
        }
    }

    //region errorConsumers : Map<Integer,Consumer<ErrMessage>>
    private final Map<Integer,Consumer<ErrMessage>> errorConsumers = new ConcurrentHashMap<>();
    public Map<Integer,Consumer<ErrMessage>> getErrorConsumers(){ return errorConsumers; }
    //endregion
    //region responseConsumers : Map<Integer,Consumer<? extends Message>>
    private final Map<Integer,Consumer<? extends Message>> responseConsumers = new ConcurrentHashMap<>();
    public Map<Integer,Consumer<? extends Message>> getResponseConsumers(){ return responseConsumers; }
    //endregion

    //region unbindedError : BiConsumer<ErrMessage,TcpHeader>
    private volatile BiConsumer<ErrMessage,TcpHeader> unbindedErrorConsumer = (err, hdr) -> {
        log().error("accept unbinded error on request {} message: {}", hdr.getReferrer(), err.getMessage());
    };
    public BiConsumer<ErrMessage,TcpHeader> unbindedError(){ return unbindedErrorConsumer; }
    public TcpProtocol unbindedError(BiConsumer<ErrMessage,TcpHeader> cons){
        //noinspection ReplaceNullCheck
        if( cons==null ){
            unbindedErrorConsumer = (err, hdr) -> {
                log().error("accept unbinded error on request {} message: {}", hdr.getReferrer(), err.getMessage());
            };
        }else{
            unbindedErrorConsumer = cons;
        }
        return this;
    }
    //endregion
    //region unbindedMessage : BiConsumer<Message,TcpHeader>
    private volatile BiConsumer<Message,TcpHeader> unbindedMessageConsumer = (msg, hdr) -> {
        log().error("accept unbinded message on request {} detail: {}", hdr.getReferrer(), msg);
    };
    public BiConsumer<Message,TcpHeader> unbindedMessage(){ return unbindedMessageConsumer; }
    public TcpProtocol unbindedMessage(BiConsumer<Message,TcpHeader> cons){
        //noinspection ReplaceNullCheck
        if( cons==null ){
            unbindedMessageConsumer = (msg, hdr) -> {
                log().error("accept unbinded message on request {} detail: {}", hdr.getReferrer(), msg);
            };
        }else{
            unbindedMessageConsumer = cons;
        }
        return this;
    }
    //endregion

    private volatile Optional<BuffInputStream> prevStream = Optional.empty();

    public boolean readNow() throws IOException {
        log().info("readNow ");

        var raw = receiveRaw(prevStream);
        if( raw.isEmpty() )return false;

        prevStream = raw.get().b();

        var rw = raw.get().a().toReadonly();
        if( !rw.isPayloadChecksumMatched() ){
            log().warn("checksum fail");
            return true;
        }

        Message msg = null;
        try{
            msg = rw.payloadMessage();
        } catch( IOError err ){
            log().error("payload de serialization fail",err);
            return true;
        }

        if( msg==null ){
            return true;
        }

        process(msg, rw.getHeader());
        return true;
    }
    protected void process( Message msg, TcpHeader header ){
        log().info("process {}, message.sid {}", msg.getClass(), header.getSid());
        if( msg instanceof Ping ){
            process((Ping) msg, header);
        }else if( msg instanceof Pong ){
            process((Pong) msg, header);
        }else if( msg instanceof ErrMessage ){
            var refIdOpt = header.getReferrer();
            if( refIdOpt.isPresent() ){
                var refId = refIdOpt.get();
                responseConsumers.remove(refId);

                var cons = errorConsumers.remove(refId);
                if( cons != null ){
                    cons.accept((ErrMessage) msg);
                    return;
                }
            }

            var cons = unbindedErrorConsumer;
            if( cons != null ){
                cons.accept((ErrMessage) msg, header);
            }
        }else if( msg instanceof ServerEvent ){
            process((ServerEvent) msg, header);
        }else{
            var refIdOpt = header.getReferrer();
            if( refIdOpt.isPresent() ){
                var refId = refIdOpt.get();
                errorConsumers.remove(refId);

                @SuppressWarnings("rawtypes") Consumer cons = responseConsumers.remove(refId);
                if( cons!=null ){
                    try {
                        //noinspection unchecked
                        cons.accept(msg);
                        return;
                    } catch( Throwable err ){
                        log().error("accept message error",err);
                    }
                }
            }

            var cons = unbindedMessageConsumer;
            if( cons!=null ){
                cons.accept(msg, header);
            }
        }
    }

    private final Queue<Consumer<Pong>> pongConsumers = new ConcurrentLinkedQueue<>();
    protected void process(Pong pong, TcpHeader header){
        while( true ){
            var cons = pongConsumers.poll();
            if( cons==null )break;
            cons.accept(pong);
        }
    }
    protected void process(Ping ping, TcpHeader header){
        try{
            var sid = header.getSid();
            if( sid.isPresent() ){
                send(new Pong(), TcpHeader.referrer.create(sid.get()));
            }else {
                send(new Pong());
            }
        } catch( IOException e ) {
            log().error("fail send pong response");
        }
    }

    protected final Map<String,Set<Consumer<ServerEvent>>> serverEventListeners = new ConcurrentHashMap<>();
    public AutoCloseable listenServerEvent( String publisher, Consumer<ServerEvent> listener ){
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        if( publisher==null )throw new IllegalArgumentException( "publisher==null" );
        synchronized( serverEventListeners ){
            serverEventListeners.computeIfAbsent(publisher, x -> new CopyOnWriteArraySet<>()).add(listener);
        }
        return () -> {
            //serverEventListeners.remove(listener);
            synchronized( serverEventListeners ){
                var ls = serverEventListeners.get(publisher);
                if( ls != null ){
                    ls.remove(listener);
                    if( ls.isEmpty() ){
                        serverEventListeners.remove(publisher);
                    }
                }
            }
        };
    }
    public void removeServerEventListener( Consumer<ServerEvent> listener ){
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        synchronized( serverEventListeners ){
            var removes = new HashSet<String>();
            for( var pub : serverEventListeners.keySet() ){
                var ls = serverEventListeners.get(pub);
                ls.remove(listener);
                if( ls.isEmpty() ){
                    removes.add(pub);
                }
            }
            removes.forEach(serverEventListeners::remove);
        }
    }

    protected void process(ServerEvent sevent, TcpHeader header){
        log().debug("process server event {}",sevent);

        var pub = sevent.getPublisher();
        if( pub!=null ){
            var sevListeners = serverEventListeners.get(pub);
            if( sevListeners != null ){
                for( var ls : sevListeners ){
                    if( ls!=null ){
                        log().debug("ls.accept");
                        ls.accept(sevent);
                    }
                }
            }
        }
    }
    //endregion

    @SuppressWarnings("UnusedReturnValue")
    public int ping(Consumer<Pong> consumer){
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        pongConsumers.add(consumer);
        try{
            return send(new Ping());
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    public ResultConsumer<Compile,CompileResult> compile(LambdaDump methodDef){
        if( methodDef==null )throw new IllegalArgumentException( "methodDef==null" );

        Compile cmpl = new Compile();
        cmpl.setDump(methodDef);
        //noinspection unchecked,rawtypes,rawtypes
        return new ResultConsumer(this, cmpl, errorConsumers, responseConsumers);
    }
    public ResultConsumer<Execute,ExecuteResult> execute(CompileResult cres){
        if( cres==null )throw new IllegalArgumentException( "cres==null" );

        Execute exec = new Execute();
        exec.setKey(cres.getKey());
        exec.setHash(cres.getHash());
        //noinspection unchecked,rawtypes,rawtypes
        return new ResultConsumer(this, exec, errorConsumers, responseConsumers);
    }
    public ResultConsumer<Subscribe, SubscribeResult> subscribe(Subscribe subscribe){
        if( subscribe==null )throw new IllegalArgumentException( "subscribe==null" );
        //noinspection unchecked,rawtypes,rawtypes
        return new ResultConsumer(this, subscribe, errorConsumers, responseConsumers);
    }
    public ResultConsumer<UnSubscribe, UnSubscribeResult> unsubscribe(UnSubscribe subscribe){
        if( subscribe==null )throw new IllegalArgumentException( "subscribe==null" );
        //noinspection unchecked,rawtypes,rawtypes
        return new ResultConsumer(this, subscribe, errorConsumers, responseConsumers);
    }
}
