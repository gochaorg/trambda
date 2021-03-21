package xyz.cofe.trambda.tcp;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

public class LOG {
    public static void level(Class logger, Level lvl){
        var lgr = LoggerFactory.getLogger(logger);
        if( lgr instanceof ch.qos.logback.classic.Logger ){
            var l = (ch.qos.logback.classic.Logger)lgr;
            l.setLevel(lvl);
        }
    }
    public static void level(Level lvl,Class ... loggers){
        for(var lg : loggers){
            level(lg,lvl);
        }
    }
}
