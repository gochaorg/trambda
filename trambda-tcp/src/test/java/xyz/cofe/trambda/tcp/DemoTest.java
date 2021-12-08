package xyz.cofe.trambda.tcp;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.trambda.Query;
import xyz.cofe.trambda.tcp.demo.Events;
import xyz.cofe.trambda.tcp.demo.IEnv;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;
import xyz.cofe.trambda.tcp.demo.OsProc;
import xyz.cofe.trambda.tcp.demo.ServerDemoEvent;

import static xyz.cofe.trambda.tcp.LOG.level;

public class DemoTest {
    private static final Logger log = LoggerFactory.getLogger(DemoTest.class);
    private int port = ThreadLocalRandom.current().nextInt(40000)+10000;

    @Test
    public void demo01(){
        System.out.println("demo01");
        System.out.println("=".repeat(60));

        level(Level.INFO, TcpProtocol.class);

        Closeables closeables = new Closeables();

        log.info("create server");

        ServerSocket ssocket = null;
        TcpServer<IEnv> server = null;
        try{
            ssocket = new ServerSocket(port);
            ssocket.setSoTimeout(1000*5);

            server = new TcpServer<IEnv>(ssocket,
                s -> new LinuxEnv(
                    s.getServer().publisher("defaultPublisher")
                )
            );
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
        var qRegex = "chrome|java";
        var responseSize = new AtomicInteger(0);
        var responseAcceptTime = new AtomicLong(0);

        Thread testRespTh = new Thread(()->{
            var response = query.apply( env ->
                env.processes().stream()
                    .filter( p -> p.getName().matches("(?is).*("+qRegex+").*") )
                    .collect(Collectors.toList())
            );

            responseAcceptTime.set(System.currentTimeMillis());

            response.stream()
                .map(OsProc::toString)
                .forEach(System.out::println);

            responseSize.set(response.size());
        });

        var requestSendTime = System.currentTimeMillis();

        testRespTh.setName("send query");
        testRespTh.setDaemon(true);
        testRespTh.start();
        try {
            testRespTh.join(1000L*10L);
        } catch (InterruptedException e) {
            System.out.println("close thread "+testRespTh.getName()+" by timeout");
            testRespTh.interrupt();
            try {
                testRespTh.join(1000L*3L);
            } catch (InterruptedException ex) {
                testRespTh.stop();
            }
        }

        log.info("shutdown");
        serv.shutdown();

        try{
            serv.join(1000L * 5L);
        } catch( InterruptedException e ) {
            log.error("serv join");
        }

        closeables.close();

        boolean requestFinished = responseAcceptTime.get()>0 && responseAcceptTime.get() >= requestSendTime;
        boolean responseAccepted = responseSize.get()>0;
        System.out.println("requestFinished = "+requestFinished);
        System.out.println("responseAccepted = "+responseAccepted);
        Assertions.assertTrue(requestFinished);
        Assertions.assertTrue(responseAccepted);
    }

    @Test
    public void demo03(){
        System.out.println("demo03");
        System.out.println("=".repeat(80));

        level(Level.INFO, TcpProtocol.class);

        Closeables closeables = new Closeables();

        log.info("create server");

        ServerSocket ssocket = null;
        TcpServer<IEnv> server = null;
        try{
            ssocket = new ServerSocket(port+1);
            ssocket.setSoTimeout(1000*5);

            server = new TcpServer<IEnv>(ssocket,
                s -> new LinuxEnv(
                    s.getServer().publishers(Events.class)
                )
            );
            server.setDaemon(true);
            server.start();
            server.setName("server");
            server.addListener(System.out::println);
            closeables.add((AutoCloseable) server);
        } catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        log.info("subscribe");

        var query = TcpQuery.create(IEnv.class).host("localhost").port(port+1).build();

        query.subscribe(Events.class, pubs -> {
            pubs.defaultPublisher().listen( msg -> {
                System.out.println("message "+msg.message);
            });
            pubs.timedEvents().listen( msg -> {
                System.out.println("t.message "+msg.message+" t="+msg.serverTime);
            });
        });

        closeables.add(query);

        log.info("send notify");
        query.apply( env -> {
            env.notifyMe(5,100);
            return List.of();
        });
        log.info("sent notify");

        log.info("send timed notify");
        query.apply( env -> {
            env.notifyMe(5,100, true);
            return List.of();
        });
        log.info("sent timed notify");

        try{
            log.info("close query");
            query.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }

        server.shutdown();

        try{
            server.join(1000L * 5L);
        } catch( InterruptedException e ) {
            log.error("serv join");
        }
    }
}
