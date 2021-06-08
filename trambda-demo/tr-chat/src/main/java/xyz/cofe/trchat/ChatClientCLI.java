package xyz.cofe.trchat;

import ch.qos.logback.classic.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.trambda.tcp.TcpQuery;

public class ChatClientCLI {
    private int port = 16800;
    public int getPort(){ return port; }
    public void setPort(int p){
        this.port = p;
    }

    private String address;
    public String getAddress(){ return address; }
    public void setAddress(String address){ this.address = address; }

    private String username;
    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }

    private final List<String> inputMessages = new CopyOnWriteArrayList<>();
    public List<String> getInputMessages(){ return inputMessages; }

    private static void verbose( boolean on ){
        if( on ){
            //LoggerFactory.getLogger("xyz.cofe.trambda.tcp.TcpProtocol").
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
                org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

            root.setLevel(Level.DEBUG);
        } else {
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
                org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

            root.setLevel(Level.WARN);
        }
    }

    public static void main(String[] argsArr){
        verbose(false);
        ChatClientCLI client = new ChatClientCLI();

        ArrayList<String> args = new ArrayList<>(Arrays.asList(argsArr));
        var state = "0";

        while( !args.isEmpty() ){
            var arg = args.remove(0);
            switch( state ){
                case "0":
                    switch( arg ){
                        case "-debug":
                            verbose(true);
                            break;
                        case "-p":
                        case "-port":
                            state = "port";
                            break;
                        case "-a":
                        case "-addr":
                        case "-address":
                            state = "addr";
                            break;
                        case "-u":
                        case "-usr":
                        case "-user":
                            state = "user";
                            break;
                        case "-m":
                        case "-message":
                            state = "message";
                            break;
                    }
                    break;
                case "user":
                    state = "0";
                    client.setUsername(arg);
                    break;
                case "port":
                    state = "0";
                    client.setPort(Integer.parseInt(arg));
                    break;
                case "addr":
                    state = "0";
                    client.setAddress(arg);
                    break;
                case "message":
                    state = "0";
                    client.getInputMessages().add(arg);
                    break;
            }
        }

        client.start();
    }

    private volatile TcpQuery<ChatService> query;

    public void start(){
        var log = LoggerFactory.getLogger(ChatClientCLI.class);
        log.info("starting chat");

        Scanner scanner = new Scanner(System.in);

        var addr = address;
        if( addr==null || addr.length()<1 ){
            System.out.print("enter server address: ");
            addr = scanner.nextLine();
        }

        query = TcpQuery.create(ChatService.class).host(addr).port(port).build();

        var uname = username;
        if( uname==null || uname.length()<1 ){
            System.out.print("enter user name: ");
            uname = scanner.nextLine();
        }

        while( true ){
            var setName = uname;
            try{
                query.apply(srvc -> {
                    srvc.setUsername(setName);
                    return List.of();
                });
                break;
            } catch( Throwable err ){
                log.error("set user name fail",err);
                System.out.print("enter user name again: ");
                uname = scanner.nextLine();
            }
        }

        query.subscribe(ChatNotification.class, notif -> {
            notif.messages().listen( msg -> {
                System.out.println("@"+msg.getUser()+" "+msg.getMessage());
            });
        });

        while( true ){
            String line = null;
            if( !inputMessages.isEmpty() ){
                line = inputMessages.remove(0);
            } else {
                System.out.print("enter message: ");
                line = scanner.nextLine();
            }

            if( line.equalsIgnoreCase("/exit") )break;

            String sendLine = line;
            var res = query.apply( srvc -> {
                srvc.echo( sendLine );
                return "ok";
            });

            System.out.println("sent "+res);
        }

        try{
            query.close();
            query = null;
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
