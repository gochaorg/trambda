package xyz.cofe.trambda.demo.serv;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.trambda.demo.api.IEnv;
import xyz.cofe.trambda.demo.api.LinuxEnv;
import xyz.cofe.trambda.tcp.TcpServer;
import xyz.cofe.trambda.tcp.TcpServerCLI;

public class ServerMain0 {
    private static final Logger log = LoggerFactory.getLogger(ServerMain0.class);
    public static void main(String[] args){
        try{
            var bindAddr = new InetSocketAddress("0.0.0.0", 9988);

            ServerSocket ss = new ServerSocket();
            ss.bind(bindAddr);

            log.info("start server {}",bindAddr);
            new TcpServer<>(ss, ses -> new LinuxEnv()).start();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
