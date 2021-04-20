package xyz.cofe.trambda.tcp.serv.cli;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyCallable;
import groovy.lang.GroovyShell;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.groovy.groovysh.ExitNotification;
import org.apache.groovy.groovysh.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.io.fs.File;
import xyz.cofe.trambda.tcp.TcpServer;

public class TcpServerCLI {
    private static final Logger log = LoggerFactory.getLogger(TcpServerCLI.class);

    public static final Map<TcpServer<Object>, ServerBuildInfo<Object>> servers = new ConcurrentHashMap<>();
    public static final Map<InetSocketAddress, ServerBuildInfo<Object>> serversBuildInfo = new ConcurrentHashMap<>();

    public Map<InetSocketAddress, ServerBuildInfo<Object>> getServersBuildInfo(){ return serversBuildInfo; }

    public void addService( Class<?> service, String host, int port ){
        if( service==null )throw new IllegalArgumentException( "service==null" );
        if( host==null )throw new IllegalArgumentException( "host==null" );
        if( port<0 )throw new IllegalArgumentException( "port<0" );
        if( port>65535 )throw new IllegalArgumentException( "port>65535" );

        log.info("add service {} host {} port {}",service,host,port);
        serversBuildInfo.put(
            new InetSocketAddress(host,port),
            new ServerBuildInfo<>().service(service));
    }

    public void start(){
        synchronized( servers ){
            serversBuildInfo.forEach((addr, bi) -> {
                log.info("starting service {} on {}", bi.service(), addr);
                try{
                    log.debug("create server socket");
                    ServerSocket serverSocket = new ServerSocket();

                    log.debug("bind server socket {}",addr);
                    serverSocket.bind(addr);

                    TcpServer<Object> serv = bi.build(serverSocket);
                    servers.put(serv, bi);

                    serv.start();
                    log.debug("server started");
                } catch( IOException e ) {
                    log.error("can't start", e);
                }
            });
        }
    }
    public void stop(){
        synchronized( servers ){
            log.info("stop services");
            servers.keySet().forEach(TcpServer::shutdown);
            servers.clear();
        }
    }

    public void service( String addr, Class<?> srvc ){
        if( addr==null )throw new IllegalArgumentException( "addr==null" );
        if( srvc==null )throw new IllegalArgumentException( "srvc==null" );
        service( addr, srvc, null );
    }
    public void service( String addr, Object srvc ){
        if( addr==null )throw new IllegalArgumentException( "addr==null" );
        if( srvc==null )throw new IllegalArgumentException( "srvc==null" );
        service( addr, srvc, null );
    }

    public void service( String addr, Class<?> srvc, Closure<?> conf ){
        if( addr==null )throw new IllegalArgumentException( "addr==null" );
        if( srvc==null )throw new IllegalArgumentException( "srvc==null" );

        log.info("registry {} on {}", srvc, addr);

        String[] ipPort = addr.split(":",2);
        if( ipPort.length!=2 ){
            throw new IllegalArgumentException("addr");
        }
        String host = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        var bi = new ServerBuildInfo<Object>().service(srvc);

        if( conf!=null ){
            var reg = new ServiceRegistry(bi);
            conf.setDelegate(reg);
            conf.setResolveStrategy(Closure.DELEGATE_FIRST);
            conf.call(reg);
        }

        InetSocketAddress iAddr = new InetSocketAddress(host,port);
        serversBuildInfo.put(iAddr, bi);
    }
    public void service( String addr, Object srvc, Closure<?> conf ){
        if( addr==null )throw new IllegalArgumentException( "addr==null" );
        if( srvc==null )throw new IllegalArgumentException( "srvc==null" );

        log.info("registry {} on {}", srvc.getClass(), addr);

        String[] ipPort = addr.split(":",2);
        if( ipPort.length!=2 ){
            throw new IllegalArgumentException("addr");
        }
        String host = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        var bi = new ServerBuildInfo<Object>().service(srvc);

        if( conf!=null ){
            var reg = new ServiceRegistry(bi);
            conf.setDelegate(reg);
            conf.setResolveStrategy(Closure.DELEGATE_FIRST);
            conf.call(reg);
        }

        InetSocketAddress iAddr = new InetSocketAddress(host,port);
        serversBuildInfo.put(iAddr, bi);
    }

    private Binding binding(){
        Binding binding = new Binding();
        binding.setProperty("app", this);
        binding.setProperty("exit", new GroovyCallable() {
            @Override
            public Object call() throws Exception{
                throw new ExitNotification(0);
            }
        });
        binding.setProperty("start", new GroovyCallable() {
            @Override
            public Object call() throws Exception{
                start();
                return null;
            }
        });
        binding.setProperty("stop", new GroovyCallable() {
            @Override
            public Object call() throws Exception{
                stop();
                return null;
            }
        });
        binding.setProperty("service", new Object() {
            public Object call(String addr, Class service ) {
                if( addr==null )throw new IllegalArgumentException( "addr==null" );
                if( service==null )throw new IllegalArgumentException( "service==null" );
                service(addr,service);
                return null;
            }
            public Object call(String addr, Object service ) {
                if( addr==null )throw new IllegalArgumentException( "addr==null" );
                if( service==null )throw new IllegalArgumentException( "service==null" );
                service(addr,service);
                return null;
            }
            public Object call(String addr, Class service, Closure<?> conf ) {
                if( addr==null )throw new IllegalArgumentException( "addr==null" );
                if( service==null )throw new IllegalArgumentException( "service==null" );
                if( conf==null )throw new IllegalArgumentException( "conf==null" );
                service(addr,service,conf);
                return null;
            }
            public Object call(String addr, Object service, Closure<?> conf ) {
                if( addr==null )throw new IllegalArgumentException( "addr==null" );
                if( service==null )throw new IllegalArgumentException( "service==null" );
                if( conf==null )throw new IllegalArgumentException( "conf==null" );
                service(addr,service,conf);
                return null;
            }
        });
        return binding;
    }
    private void interactive(){
        log.info("interactive()");

        Scanner scanner = new Scanner(System.in);

        Groovysh sh = new Groovysh(binding(), new IO());
        while( true ){
            System.out.print(sh.renderPrompt());
            var line = scanner.nextLine();
            try {
                sh.execute(line);
            } catch( ExitNotification exit ){
                return;
            } catch( Throwable err ){
                log.error("cli",err);
            }
        }
    }
    private void executeScript(File file, Charset cs){
        log.info("executeScript( \"{}\", {} )",file,cs);
        GroovyShell gs = new GroovyShell(binding());
        gs.run(file.readText(cs),file.getName(),new String[]{});
    }

    private void help(){
        var resources = new String[]{ "help_"+Locale.getDefault().getLanguage()+".txt", "help.txt" };
        for( var resName : resources ){
            var resUrl = TcpServerCLI.class.getResource(resName);
            if( resUrl!=null ){
                try{
                    System.out.println(IOFun.readText(resUrl,"utf-8"));
                } catch( IOException e ) {
                    e.printStackTrace();
                }
                return;
            }
        }

        System.out.println("help not found");
    }

    public static void main(String[] args0){
        log.info("starting {}",TcpServerCLI.class.getName());
        List<String> args = new ArrayList<>(List.of(args0));

        var state = "init";

        Class<?> service = null;
        String host = "0.0.0.0";
        int port = 9988;

        Charset cs = StandardCharsets.UTF_8;
        var cli = new TcpServerCLI();

        while( !args.isEmpty() ){
            String arg = args.remove(0);
            switch( state ){
                case "init":
                    switch( arg ){
                        case "service":
                            state = "service";
                            break;
                        case "port":
                            state = "port";
                            break;
                        case "host":
                            state = "host";
                            break;
                        case "add":
                            cli.addService(service, host, port);
                            service = null;
                            port++;
                            break;
                        case "-i":
                            cli.interactive();
                            break;
                        case "-s":
                            state = "-s";
                            break;
                        case "-cs":
                            state = "-cs";
                            break;
                        case "start":
                            cli.start();
                            break;
                        case "help":
                        case "/help":
                        case "/?":
                        case "-help":
                        case "--help":
                        case "?":
                            cli.help();
                            break;
                        default:
                            System.err.println("undefined argument "+arg);
                    }
                    break;
                case "-cs":
                    state = "init";
                    cs = Charset.forName(arg);
                    break;
                case "-s":
                    state = "init";
                    var scriptFile = new File(arg);
                    //cli.interactive(new Scanner(scriptFile.readText(cs)));
                    cli.executeScript(scriptFile,cs);
                    break;
                case "service":
                    state = "init";
                    try{
                        service = Class.forName(arg);
                    } catch( ClassNotFoundException e ) {
                        log.error("service class not found",e);
                    }
                    break;
                case "port":
                    state = "init";
                    port = Integer.parseInt(arg);
                    break;
                case "host":
                    state = "init";
                    host = arg;
                    break;
            }
        }
    }
}
