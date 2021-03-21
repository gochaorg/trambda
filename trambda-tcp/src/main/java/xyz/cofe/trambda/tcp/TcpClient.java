package xyz.cofe.trambda.tcp;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClient implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TcpClient.class);
    protected final Socket socket;
    protected final TcpProtocol proto;
    private final Thread socketReaderThread;

    public TcpClient(Socket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;
        this.proto = new TcpProtocol(socket);
        socketReaderThread = new Thread(this::reader);
        socketReaderThread.setDaemon(true);
        socketReaderThread.setName("client "+socket.getRemoteSocketAddress());
        socketReaderThread.start();
    }

    private void reader(){
        log.info("reader started");
        while( true ){
            try{
                var raw = proto.receiveRaw();
                if( raw.isEmpty() )break;

                var rw = raw.get().toReadonly();
                if( !rw.isPayloadChecksumMatched() ){
                    log.warn("checksum fail");
                }

                Message msg = null;
                try{
                    msg = rw.payloadMessage();
                } catch( IOError err ){
                    log.error("payload de serialization fail",err);
                    continue;
                }

                if( msg==null ){
                    continue;
                }

                process(msg, rw.getHeader());
            } catch( SocketTimeoutException e ) {
                log.trace("socket timeout");
                continue;
            } catch( IOException e ) {
                log.error("socket",e);
                break;
            }
        }
        log.info("reader closed");
    }

    protected void process(Message msg, TcpHeader header){
        if(msg instanceof Pong ){
            while( true ){
                var cons = pongConsumers.poll();
                if( cons==null )break;
                cons.accept((Pong) msg);
            }
        }
    }

    private final Queue<Consumer<Pong>> pongConsumers = new ConcurrentLinkedQueue<>();
    public void ping(Consumer<Pong> consumer){
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        pongConsumers.add(consumer);
        try{
            int msgId = proto.send(new Ping());
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    @Override
    public synchronized void close() throws Exception{
        if( socketReaderThread.getId()==Thread.currentThread().getId() ){
            throw new IllegalStateException("can't close from self");
        }

        if( socketReaderThread.isAlive() ){
            socketReaderThread.interrupt();
            try{
                socketReaderThread.join(1000L * 10L);
            }catch( InterruptedException e ){
                log.error("socketReaderThread not respond");
                if( !socket.isClosed() ){
                    try {
                        log.info("close socket");
                        socket.close();
                    } catch( IOException ex ){
                        log.error("socket not closed",ex);
                    }
                }
                log.warn("terminate socketReaderThread");
                socketReaderThread.stop();
            }
        }

        if( !socket.isClosed() ){
            try {
                log.info("close socket");
                socket.close();
            } catch( IOException ex ){
                log.error("socket not closed",ex);
            }
        }
    }
}
