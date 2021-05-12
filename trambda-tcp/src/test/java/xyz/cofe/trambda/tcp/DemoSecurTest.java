package xyz.cofe.trambda.tcp;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.Query;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.SecurityFilters;
import xyz.cofe.trambda.tcp.demo.IEnv;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;
import xyz.cofe.trambda.tcp.demo.OsProc;

import static xyz.cofe.trambda.tcp.LOG.level;

public class DemoSecurTest {
    private static final Logger log = LoggerFactory.getLogger(DemoSecurTest.class);
    private int port = ThreadLocalRandom.current().nextInt(40000)+10000;

    @Test
    public void test01(){
        System.out.println(DemoSecurTest.class.getSimpleName()+".test01");
        System.out.println("=".repeat(80));

        AtomicReference<MethodDef> mdefRef = new AtomicReference<>();

        Query<IEnv> query = new AsmQuery<IEnv>(){
            /**
             * Реализация вызова лямбды
             *
             * @param fn   лямбда
             * @param sl   лямбда - сериализация
             * @param mdef байт-код лямбды
             * @return результат вызова
             */
            @Override
            protected <RES> RES call(Fn1<IEnv, RES> fn, SerializedLambda sl, MethodDef mdef){
                mdefRef.set(mdef);
                return super.call(fn, sl, mdef);
            }
        };

        var qRegex = "chrome|java";
        query.apply(env ->
            env.processes().stream()
                .filter( p -> p.getName().matches("(?is).*("+qRegex+").*") )
                .collect(Collectors.toList())
        );

        System.out.println();
        System.out.println("inspect");
        System.out.println("-".repeat(80));

        SecurityFilters.create(s -> {
            s.allow( a -> {
                a.invoke("demo api", c->c.getOwner().matches("xyz\\.cofe\\.trambda\\.tcp\\.demo\\.([\\w\\d]+)"));
                a.invoke("java collections api", c->c.getOwner().matches(
                    "java\\.util\\.(List)|java\\.util\\.stream\\.([\\w\\d]+)"));
                a.invoke("java lang api", c->c.getOwner().matches("java\\.lang\\.(String)"));
                a.invoke("java compiler", c->c.getOwner().matches("java\\.lang\\.invoke\\.(LambdaMetafactory|StringConcatFactory)"));
            });
            s.deny().any("by default");
        })
            .validate(SecurAccess.inspect(mdefRef.get()))
            .forEach(System.out::println);
    }

    @Test
    public void test02(){
        System.out.println(DemoSecurTest.class.getSimpleName()+".test02");
        System.out.println("=".repeat(80));

        level(Level.INFO, TcpProtocol.class);

        Closeables closeables = new Closeables();

        log.info("create server");

        ServerSocket ssocket = null;
        TcpServer<IEnv> server = null;
        try{
            ssocket = new ServerSocket(port);
            ssocket.setSoTimeout(1000*5);

            server = new TcpServer<IEnv>(
                ssocket,
                s -> new LinuxEnv(),
                SecurityFilters.create(s -> {
                    s.allow( a -> {
                        a.invoke("demo api", c->c.getOwner().matches("xyz\\.cofe\\.trambda\\.tcp\\.demo\\.([\\w\\d]+)"));
                        a.invoke("java collections api", c->c.getOwner().matches(
                            "java\\.util\\.(List)|java\\.util\\.stream\\.([\\w\\d]+)"));
                        a.invoke("java lang api", c->c.getOwner().matches("java\\.lang\\.(String)"));
                        a.invoke("java compiler", c->c.getOwner().matches("java\\.lang\\.invoke\\.(LambdaMetafactory|StringConcatFactory)"));
                    });
                    s.deny().any("by default");
                })
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

        var tcpQuery = TcpQuery.create(IEnv.class).host("localhost").port(port).build();
        closeables.add(tcpQuery);

        //noinspection UnnecessaryLocalVariable
        Query<IEnv> query = tcpQuery;

        System.out.println();
        System.out.println("first valid query");
        System.out.println("- ".repeat(40));

        var qRegex = "chrome|java";
        query.apply(env ->
            env.processes().stream()
                .filter( p -> p.getName().matches("(?is).*("+qRegex+").*") )
                .collect(Collectors.toList())
        ).stream().map(OsProc::toString).forEach(log::info);

        System.out.println();
        System.out.println("second invalid query");
        System.out.println("- ".repeat(40));

        try{
            query.apply(env -> {
                    System.gc();
                    return env.processes().stream()
                        .filter(p -> p.getName().matches("(?is).*(" + qRegex + ").*"))
                        .collect(Collectors.toList());
                }
            ).stream().map(OsProc::toString).forEach(log::info);
        } catch( Throwable err ){
            log.error("error lambda call", err);
        }

        log.info("shutdown");
        server.shutdown();

        try{
            server.join(1000L * 5L);
        } catch( InterruptedException e ) {
            log.error("server join");
        }

        closeables.close();
    }
}
