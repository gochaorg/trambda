package xyz.cofe.trchat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.trambda.tcp.TcpServer;

public class ChatServerCLI {
    private static final Logger log = LoggerFactory.getLogger(ChatServerCLI.class);

    private int port = 16800;
    public int getPort(){ return port; }
    public void setPort(int p){
        this.port = p;
    }

    private int soTimeout = 1000*5;
    public int getSoTimeout(){ return soTimeout; }
    public void setSoTimeout(int v){
        soTimeout = v;
    }

    private InetAddress bindAddress;
    public InetAddress getBindAddress(){ return bindAddress; }
    public void setBindAddress(InetAddress bindAddress){ this.bindAddress = bindAddress; }

    private volatile ServerSocket serverSocket;
    public ServerSocket getServerSocket(){
        return serverSocket;
    }

    private boolean daemon = false;
    public boolean isDaemon(){ return daemon; }
    public void setDaemon(boolean daemon){
        this.daemon = daemon;
    }

    private volatile TcpServer<ChatService> server;
    public TcpServer<ChatService> getServer(){
        return server;
    }

    public void start(){
        try{
            log.info("starting server");

            if( bindAddress!=null ){
                log.info("bind socket to port={}, backlog={}, bindAddress={}", port, 0, bindAddress);
                serverSocket = new ServerSocket(port,0,bindAddress);
            } else {
                log.info("bind socket to port={}", port);
                serverSocket = new ServerSocket(port);
            }

            log.info("set so timeout {}",soTimeout);
            serverSocket.setSoTimeout(soTimeout);

            server = new TcpServer<ChatService>(
                serverSocket,
                ses -> new ChatService(ses, ses.getServer().publishers(ChatNotification.class))
            );

            log.info("set daemon {}", daemon);
            server.setDaemon(daemon);

            log.info("start");
            server.setName("server");
            server.start();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argsArr){
        ChatServerCLI serv = new ChatServerCLI();

        ArrayList<String> args = new ArrayList<>(Arrays.asList(argsArr));
        var state = "0";
        while( !args.isEmpty() ){
            var arg = args.remove(0);
            switch( state ){
                case "0":
                    switch( arg ){
                        case "-port":
                            state = "port";
                            break;
                        case "-soTimeout":
                            state = "soTimeout";
                            break;
                        case "-addr":
                            state = "addr";
                            break;
                        case "-daemon":
                            state = "daemon";
                            break;
                        default:
                            System.out.println("undefined arg "+arg);
                            break;
                    }
                    break;
                case "daemon":
                    state = "0";
                    serv.setDaemon( Boolean.parseBoolean(arg) );
                    break;
                case "port":
                    state = "0";
                    serv.setPort( Integer.parseInt(arg) );
                    break;
                case "soTimeout":
                    state = "0";
                    serv.setSoTimeout( Integer.parseInt(arg) );
                    break;
                case "addr":
                    state = "0";
                    try{
                        serv.setBindAddress( InetAddress.getByName(arg) );
                    } catch( UnknownHostException e ) {
                        log.error("UnknownHostException for "+arg,e);
                    }
                    break;
            }
        }

        serv.start();
    }
}
