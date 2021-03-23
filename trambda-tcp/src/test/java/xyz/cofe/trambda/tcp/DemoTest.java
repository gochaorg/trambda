package xyz.cofe.trambda.tcp;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.trambda.Query;
import xyz.cofe.trambda.tcp.demo.IEnv;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;
import xyz.cofe.trambda.tcp.demo.OsProc;

import static xyz.cofe.trambda.tcp.LOG.level;

public class DemoTest {
    private static final Logger log = LoggerFactory.getLogger(DemoTest.class);
    private int port = ThreadLocalRandom.current().nextInt(40000)+10000;

    //@Test
    public void demo01(){
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

        var serv = server;

        Query<IEnv> query =  TcpQuery.create(IEnv.class).host("localhost").port(port).build();
        query.apply( env ->
            env.processes().stream()
                .filter( p -> p.getName().matches("(?is).*(chrome|java).*") )
                .collect(Collectors.toList())
        ).stream().map(OsProc::toString).forEach(log::info);

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
