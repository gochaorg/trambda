package xyz.cofe.trambda.tcp;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;

import static xyz.cofe.trambda.tcp.LOG.*;

public class PingPongTest {
    private static final Logger log = LoggerFactory.getLogger(PingPongTest.class);
    private int port = ThreadLocalRandom.current().nextInt(40000)+10000;

    //@Test
    public void test02(){
        for( int i=0; i<10; i++ ){
            test01();
        }
    }

    //@Test
    public void test01(){
        System.out.println("test01");

        level(Level.INFO, TcpClient.class, TcpSession.class, TcpProtocol.class);

        Closeables closeables = new Closeables();

        TcpServer server = null;
        try{
            ServerSocket ssocket = new ServerSocket(port);
            ssocket.setSoTimeout(1000*5);
            System.out.println("socket created "+ssocket.getLocalSocketAddress());

            server = new TcpServer(ssocket,s -> new LinuxEnv());
            server.setDaemon(true);
            server.start();
            server.setName("server");
            server.addListener(System.out::println);
        } catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        closeables.add((AutoCloseable) server);

        Socket sock = null;
        TcpClient client = null;
        try{
            sock = new Socket("localhost", port);
            client = new TcpClient(sock);
            closeables.add(client);
        } catch( IOException e ) {
            e.printStackTrace();
        }

        if( client!=null ){
            var cl = client;
            var serv = server;
            client.ping(pong -> {
                log.info("accept pong");
                try{
                    log.info("stopping server");
                    serv.shutdown();
                } catch( Exception e ) {
                    e.printStackTrace();
                }
            });
        }

        try{
            server.join(1000L * 30L);
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }

        System.out.println("stopping");
        closeables.close();
    }
}
