package xyz.cofe.trambda.tcp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.fn.Tuple2;

public class TcpServerCLI {
    private static final Logger log = LoggerFactory.getLogger(TcpServerCLI.class);

    public static final Map<TcpServer<Object>, Tuple2<Class,Class>> servers = new ConcurrentHashMap<>();
    private static final Map<InetSocketAddress, Tuple2<Class,Class>> serversBuildInfo = new ConcurrentHashMap<>();

    public void addService( Class<?> service, Class<?> impl, String host, int port ){
        if( service==null )throw new IllegalArgumentException( "service==null" );
        if( impl==null )throw new IllegalArgumentException( "impl==null" );
        if( host==null )throw new IllegalArgumentException( "host==null" );
        if( port<0 )throw new IllegalArgumentException( "port<0" );
        if( port>65535 )throw new IllegalArgumentException( "port>65535" );
        if( !service.isAssignableFrom(impl) ){
            throw new IllegalArgumentException(impl+" not instance of "+service);
        }

        log.info("add service {} impl {} host {} port {}",service,impl,host,port);
        serversBuildInfo.put(new InetSocketAddress(host,port),Tuple2.of(service,impl));
    }

    public void addService( Class<?> service, String host, int port ){
        if( service==null )throw new IllegalArgumentException( "service==null" );
        if( host==null )throw new IllegalArgumentException( "host==null" );
        if( port<0 )throw new IllegalArgumentException( "port<0" );
        if( port>65535 )throw new IllegalArgumentException( "port>65535" );
        addService(service,service,host,port);
    }

    public void start(){
        synchronized( servers ){
            serversBuildInfo.forEach((addr, services) -> {
                log.info("starting service {} impl {} on {}", services.a(), services.b(), addr);
                try{
                    log.debug("create server socket");
                    ServerSocket serverSocket = new ServerSocket();

                    log.debug("bind server socket");
                    serverSocket.bind(addr);

                    log.debug("create serice object from {}", services.b());
                    Object inst = null;
                    try{
                        inst = services.b().getDeclaredConstructor().newInstance();
                    } catch( InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
                        log.error("can't service object");
                        return;
                    }

                    var ist = inst;
                    TcpServer<Object> serv = new TcpServer<Object>(serverSocket, ses -> ist);
                    servers.put(serv, services);

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

    private void exit(){
        stop();
    }

    private void interactive(Scanner scanner){
        if( scanner==null )throw new IllegalArgumentException( "scanner==null" );

        var addServPtrn = Pattern.compile(
            "(?is)" +
                "(add\\s+)?" +
                "service\\s+(?<srvc>\\S+)(\\s+impl\\s+(?<impl>\\S+))?" +
                "(\\s+(?<host>[^:+](:(?<port>\\d+))))"
        );
        while( true ){
            try{
                System.out.println("#>>");
                var line = scanner.nextLine();
                line = line.trim();
                if( line.startsWith("#") ){
                    continue;
                }

                var addServ = addServPtrn.matcher(line);
                if( addServ.matches() ){
                    var srvcClsName = addServ.group("srvc");
                    var implClsName = addServ.group("impl");
                    var host = addServ.group("host");
                    var portStr = addServ.group("port");
                    try{
                        Class srvcCls = Class.forName(srvcClsName);
                        Class implCls = implClsName!=null && implClsName.length()>0 ? Class.forName(implClsName) : null;
                        if( implCls!=null ){
                            addService(srvcCls,implCls,host,Integer.parseInt(portStr));
                        }else{
                            addService(srvcCls,host,Integer.parseInt(portStr));
                        }
                    } catch( ClassNotFoundException e ) {
                        log.error("class not found",e);
                    }
                    continue;
                }

                if( line.matches("(?i)start") ){
                    start();
                }

                if( line.matches("(?i)stop") ){
                    stop();
                }

                if( line.matches("(?i)exit") ){
                    exit();
                }

                if( line.matches("(?i)help") ){
                    help();
                }
            } catch( NoSuchElementException e ) {
                log.debug("EOF");
                break;
            }
        }
    }

    private void help(){
    }

    public static void main(String[] args0){
        List<String> args = new ArrayList<>(List.of(args0));

        var state = "init";

        Class service = null;
        Class impl = null;
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
                        case "impl":
                            state = "impl";
                            break;
                        case "port":
                            state = "port";
                            break;
                        case "host":
                            state = "host";
                            break;
                        case "add":
                            if( impl!=null ){
                                cli.addService(service, impl, host, port);
                            }else{
                                cli.addService(service, host, port);
                            }
                            service = null;
                            impl = null;
                            port++;
                            break;
                        case "-i":
                            cli.interactive(new Scanner(System.in));
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
                    break;
                case "-s":
                    state = "init";
                    break;
                case "service":
                    state = "init";
                    try{
                        service = Class.forName(arg);
                    } catch( ClassNotFoundException e ) {
                        log.error("service class not found",e);
                    }
                    break;
                case "impl":
                    state = "init";
                    try{
                        impl = Class.forName(arg);
                    } catch( ClassNotFoundException e ) {
                        log.error("implementation class not found",e);
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
