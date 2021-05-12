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
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;
import static xyz.cofe.trambda.tcp.LOG.*;

public class CompileTest {
    private static final Logger log = LoggerFactory.getLogger(CompileTest.class);
    private int port = ThreadLocalRandom.current().nextInt(40000)+10000;

    //@Test
    public void compileTest01(){
        System.out.println("compileTest01");
        System.out.println("=".repeat(60));

        level(Level.INFO, TcpProtocol.class);

        Closeables closeables = new Closeables();

        log.info("create server");

        ServerSocket ssocket = null;
        TcpServer server = null;
        try{
            ssocket = new ServerSocket(port);
            ssocket.setSoTimeout(1000*5);

            server = new TcpServer(ssocket,s -> new LinuxEnv());
            server.setDaemon(true);
            server.start();
            server.setName("server");
            server.addListener(System.out::println);
            closeables.add((AutoCloseable) server);
        } catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        log.info("create client");
        Socket csock = null;
        TcpClient client = null;

        try{
            csock = new Socket("localhost", port);
            client = new TcpClient(csock);
            closeables.add(client);
        } catch( IOException e ) {
            e.printStackTrace();
            closeables.close();
            return;
        }

        LambdaDump mdef = new LambdaDump();

        log.info("async send");

        var serv = server;
        synchronized( serv ){
            client.compile(mdef)
                .onSuccess(cres -> {
                    log.info("compile success, key: " + cres.getKey());
                    synchronized( serv ){
                        serv.notify();
                    }
                })
                .onFail(err -> {
                    log.info("compile fail, message: " + err.getMessage());
                    synchronized( serv ){
                        serv.notify();
                    }
                })
                .send();
            try {
                serv.wait(1000L * 10L);
            } catch( InterruptedException e ) {
                log.error("send wait",e);
            }
        }

        log.info("sync fetch");

        var cres = client.compile(mdef).fetch();
        log.info("compile res key: "+cres.getKey());

        log.info("shutdown");
        serv.shutdown();

        try{
            serv.join(1000L * 5L);
        } catch( InterruptedException e ) {
            log.error("serv join");
        }

        closeables.close();
    }
}
