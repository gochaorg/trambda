package xyz.cofe.trchat;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import xyz.cofe.trambda.tcp.TcpServer;
import xyz.cofe.trambda.tcp.TcpSession;

public class ChatService {
    private final TcpSession<ChatService> session;

    public ChatService(){
        this( null, null );
    }

    public ChatService(TcpSession<ChatService> session, ChatNotification notification){
        this.session = session;
        this.notification = notification;
    }

    private ChatNotification notification;

    private volatile String username;

    public String getUsername(){
        return username;
    }

    public void setUsername(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( !name.matches("(?is)[\\w\\d_\\-]+") )throw new IllegalArgumentException( "name not match (?is)[\\w\\d_\\-]+" );

        var curSession = session;
        if( curSession==null )return;

        synchronized( ChatService.class ){
            curSession.getServer().getSessions().forEach(ses -> {
                if( name.equalsIgnoreCase(ses.getName()) ){
                    throw new IllegalArgumentException("username \""+name+"\" already defined");
                }
            });

            username = name;
        }
    }

    public Set<String> getUserNames(){
        var curSession = session;
        if( curSession==null )return Set.of();

        return curSession.getServer().getSessions().stream()
            .map( s -> s.getService().getUsername() )
            .collect(Collectors.toSet());
    }

    public void echo( String message ){
        if( message==null )throw new IllegalArgumentException( "message==null" );

        var uname = getUsername();
        if( uname==null )throw new IllegalStateException("username is empty");

        var notify = notification;
        if( notify!=null ){
            notification.messages().publish( new ChatMessage(uname, message) );
        }
    }
}
