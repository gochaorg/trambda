package xyz.cofe.trambda.tcp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.sec.SecurMessage;

public class SecurError extends Error {
    public SecurError(String message){
        super(message);
        securMessages = List.of();
    }

    public SecurError(String message, Throwable cause){
        super(message, cause);
        securMessages = List.of();
    }

    public SecurError(Throwable cause){
        super(cause);
        securMessages = List.of();
    }

    protected SecurError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
        securMessages = List.of();
    }

    private static String extractMessage(List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> secMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Lambda contains denied byte code:").append("\n");
        if( secMessages!=null){
            for( var msg : secMessages ){
                if( msg==null || msg.isAllow() )continue;
                sb.append(msg.getMessage()).append(" for ").append(msg.getAccess()).append("\n");
            }
        }
        return sb.toString();
    }
    public SecurError(List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> secMessages){
        super(extractMessage(secMessages),null,false,false);
        securMessages = List.copyOf(secMessages);
    }

    private final List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> securMessages;
    public List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> getSecurMessages(){ return securMessages; }
}
