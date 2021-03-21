package xyz.cofe.trambda.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer extends Thread implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    protected final ServerSocket socket;
    protected final Set<TcpSession> sessions;

    public TcpServer(ServerSocket socket){
        if( socket==null )throw new IllegalArgumentException( "socket==null" );
        this.socket = socket;
        sessions = new ConcurrentSkipListSet<>();
    }

    @Override
    public void run(){
        while( true ){
            if( Thread.currentThread().isInterrupted() ){
                log.info("interrupted");
                break;
            }

            if( socket.isClosed() ){
                log.info("socket closed");
                break;
            }

            try{
                sessions.add(create(socket.accept()));
            } catch( SocketTimeoutException e ){
                log.warn("SocketTimeoutException");
                if( Thread.currentThread().isInterrupted() ){
                    log.info("interrupted");
                    break;
                }
            } catch( SocketException e ){
                if( e.getMessage()!=null && e.getMessage().matches("(?i).*socket\\s+closed.*") ){
                    log.info("socket closed");
                }else{
                    log.error("socket err",e);
                }
                break;
            } catch( IOException e ) {
                log.error("accept", e);
                break;
            }
        }

        closeSocket();
        closeSessions();
    }

    protected TcpSession create(Socket sock){
        TcpSession ses = new TcpSession(sock);
        try{
            sock.setSoTimeout(1000*3);
        } catch( SocketException e ) {
            log.warn("can't set so timeout");
        }
        ses.setDaemon(true);
        ses.setName("session#"+ses.id+"("+sock.getRemoteSocketAddress()+")");
        log.info("starting session {}",ses.getName());
        ses.start();
        return ses;
    }

    public synchronized void shutdown(){
        log.info("shutdown");
        closeSocket();
        closeSessions();
    }

    protected void closeSocket(){
        if( !socket.isClosed() ){
            try{
                log.info("close socket");
                socket.close();
            } catch( IOException e ) {
                log.info("socket close error",e);
            }
        }
    }

    protected void closeSessions(){
        log.info("close sessions");
        for( var ses : sessions ){
            if( ses.isAlive() ){
                ses.close();
                try{
                    ses.join(1000L * 5L );
                } catch( InterruptedException e ) {
                    log.warn("session {} close not responsed", ses.id);
                    ses.stop();
                }
            }
        }
    }

    @Override
    public void close() throws Exception{
        shutdown();
    }
}
