package xyz.cofe.trambda.tcp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.text.Text;

import static xyz.cofe.trambda.tcp.TcpHeader.encode;

public class TcpProtocol {
    public TcpProtocol(Socket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;

        getOutput = null;
        getInput = null;
        var l = LoggerFactory.getLogger(TcpProtocol.class);
        getLogger = ()->l;
    }
    public TcpProtocol(Socket socket, Logger logger){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;

        getOutput = null;
        getInput = null;
        getLogger = ()->logger;
    }
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

    private OutputStream output() throws IOException {
        if( socket!=null )return socket.getOutputStream();
        if( getOutput==null )throw new IllegalStateException("getOutput==null");
        return getOutput.get();
    }
    private InputStream intput() throws  IOException {
        if( socket!=null )return socket.getInputStream();
        if( getInput==null )throw new IllegalStateException("getInput==null");
        return getInput.get();
    }
    private Logger log() {
        return getLogger.get();
    }

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
        //noinspection unchecked,rawtypes
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

    public Optional<RawPack> receiveRaw() throws IOException {
        log().debug("receiveRaw()");

        int buffSize = 1024 * 8;
        var bufStrm = new BuffInputStream(intput(),buffSize);
        byte[] buff = new byte[buffSize];
        log().trace("buffer size {}",buffSize);

        while( true ){
            log().debug("mark {}",buffSize);
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
                    log().error("read fail", ex);
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

            log().trace("received h.size {} method {} h.payload {} d.payload {}",
                header.getHeaderSize(),
                header.getMethodName(),
                header.getPayloadSize(),
                rpack.getPayload().length
            );

            return Optional.of(rpack);
        }
    }
}
