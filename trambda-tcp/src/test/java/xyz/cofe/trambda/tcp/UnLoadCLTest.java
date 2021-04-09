package xyz.cofe.trambda.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.Query;
import xyz.cofe.trambda.tcp.demo.IEnv;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;
import xyz.cofe.trambda.tcp.demo.OsProc;

public class UnLoadCLTest {
    final static int port = 16789;

    public static void main(String[] args){
        ServerSocket ssocket = null;
        TcpServer server = null;
        try{
            ssocket = new ServerSocket(port);
            ssocket.setSoTimeout(1000*5);

            server = new TcpServer(ssocket,s -> new LinuxEnv());
            server.setDaemon(false);
            server.start();
            server.setName("server");
            server.addListener(System.out::println);
            //closeables.add((AutoCloseable) server);
        } catch( IOException e ) {
            e.printStackTrace();
            return;
        }
    }

    protected void client(Consumer<Query<IEnv>> c){
        var q = TcpQuery.create(IEnv.class).host("localhost").port(port).build();

        c.accept(q);

        try{
            q.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    @Tag(T.Manual)
    @Test
    public void stopServ(){
        client( q->q.apply( env -> { env.exit(0); return 0; } ) );
    }

    @Tag(T.Manual)
    @Test
    public void runGc(){
        client( q->{
            q.apply( e -> { e.doGc(); return e.memInfo();} )
                .forEach( (k,v) -> System.out.println(k+"="+v) );
        });
    }

    @Tag(T.Manual)
    @Test
    public void runFin(){
        client( q->{
            q.apply( e -> { e.runFinalization(); return e.memInfo();} )
                .forEach( (k,v) -> System.out.println(k+"="+v) );
        });
    }

    @Tag(T.Manual)
    @Test
    public void getProcs(){
        var qRegex = "chrome|java";
        client( q->
            q.apply( env ->
                env.processes().stream()
                    .filter( p -> p.getName().matches("(?is).*("+qRegex+").*") )
                    .collect(Collectors.toList())
            ).stream().map(OsProc::toString).forEach(System.out::println)
        );
    }

    @Test
    public void getProcs2(){
        var qRegex = "chrome|java";
        client( q-> {
            int qi = 0;
            long t0 = System.currentTimeMillis();
            while( true ){
                long tDiff = System.currentTimeMillis() - t0;
                if( tDiff>1000L*60L )break;

                qi++;
                System.out.println("send query "+qi);
                q.apply(env ->
                    env.processes().stream()
                        .filter(p -> p.getName().matches("(?is).*(" + qRegex + ").*"))
                        .collect(Collectors.toList())
                ).stream().map(OsProc::toString).forEach(System.out::println);

                try{
                    System.out.println("sleep");
                    Thread.sleep(5000L);
                } catch( InterruptedException e ) {
                    break;
                }
            }
        });
    }
}
