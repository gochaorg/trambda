package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpSession extends Thread implements Comparable<TcpSession> {
    private static final Logger log = LoggerFactory.getLogger(TcpSession.class);

    private static final AtomicInteger idSeq = new AtomicInteger();
    public final int id = idSeq.incrementAndGet();

    protected final Socket socket;
    protected final TcpProtocol proto;

    public TcpSession(Socket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;
        this.proto = new TcpProtocol(socket);
    }

    //region socketInfo
    public SocketAddress getLocalAddress(){ return socket.getLocalSocketAddress(); }
    public SocketAddress getRemoteAddress(){ return socket.getRemoteSocketAddress(); }
    public boolean isBound(){ return socket.isBound(); }
    public boolean isClosed(){ return socket.isClosed(); }
    public boolean isInputShutdown(){ return socket.isInputShutdown(); }
    public boolean isOutputShutdown(){ return socket.isOutputShutdown(); }
    public Optional<Boolean> getKeepAlive(){
        try{
            return Optional.of(socket.getKeepAlive());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }
    public Optional<Boolean> getTcpNoDelay(){
        try{
            return Optional.of(socket.getTcpNoDelay());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }
    public Optional<Boolean> getReuseAddress(){
        try{
            return Optional.of(socket.getReuseAddress());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }
    public Optional<Boolean> getOOBInline(){
        try{
            return Optional.of(socket.getOOBInline());
        } catch( SocketException e ) {
            log.warn("socket info",e);
            return Optional.empty();
        }
    }
    //endregion

    //region equals(), hashCode(), compareTo()
    @Override
    public boolean equals(Object o){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        TcpSession that = (TcpSession) o;
        return id == that.id;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    @Override
    public int compareTo(TcpSession o){
        if( o==null )return -1;
        return Integer.compare(id, o.id);
    }
    //endregion

    public void close(){
        if( !socket.isClosed() ){
            try{
                socket.close();
            } catch( IOException e ) {
                log.error("socket info",e);
            }
        }
    }

    @Override
    public void run(){
        while( true ){
            try{
                var rpack = proto.receiveRaw();
                if( rpack.isEmpty() ) break;

                received(rpack.get().toReadonly());
            } catch( SocketTimeoutException e ) {
                log.debug("io err, session={} {}", id, e);
                System.out.println("try repeat read");
            } catch( SocketException e ){
                if( e.getMessage()!=null && e.getMessage().matches("(?i).*socket\\s+closed.*") ){
                    log.info("socket closed");
                }else{
                    log.error("socket err",e);
                }
                break;
            } catch( IOException e ) {
                log.warn("io err, session={} {}",id,e);
                break;
            }
        }

        if( !socket.isClosed() ){
            try{
                log.info("close socket {}",socket.getRemoteSocketAddress());
                socket.close();
            } catch( IOException e ) {
                log.warn("socket close error");
            }
        }
    }

    protected void received( RawPackReadonly pack ){
        if( !pack.isPayloadChecksumMatched() ){
            log.warn("received bad payload");
            return;
        }

        Message msg = null;
        try{
            msg = pack.payloadMessage();
        } catch( IOError err ){
            log.error("payload de serialization fail",err);
            return;
        }

        if( msg==null ){
            return;
        }

        process(msg, pack.getHeader());
    }
    protected void process(Message msg, TcpHeader header){
        if(msg instanceof Ping ){
            process((Ping) msg, header);
        }
    }
    protected void process(Ping ping, TcpHeader header){
        try{
            var sid = header.getSid();
            if( sid.isPresent() ){
                proto.send(new Pong(), TcpHeader.referrer.create(sid.get()));
            }else {
                proto.send(new Pong());
            }
        } catch( IOException e ) {
            log.error("fail send response");
        }
    }
}
