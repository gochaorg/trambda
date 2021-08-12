package xyz.cofe.trambda.log.api.spi;

import xyz.cofe.trambda.log.api.Logger;
import xyz.cofe.trambda.log.api.impl.MultiLogger;
import xyz.cofe.trambda.log.api.impl.SimpleLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerRegistry {
    private static volatile LoggerRegistry instance;
    public static LoggerRegistry getInstance(){
        if( instance!=null )return instance;
        synchronized (LoggerRegistry.class){
            if( instance!=null )return instance;
            instance = new LoggerRegistry();
            return instance;
        }
    }

    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();
    public Logger getLogger(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return loggers.computeIfAbsent(name, this::createLogger);
    }

    private final List<LoggerFactory> factories;
    {
        var ls = new ArrayList<LoggerFactory>();
        for( var lf : ServiceLoader.load(LoggerFactory.class) ){
            if( lf!=null ){
                ls.add(lf);
            }
        }

        if( ls.isEmpty() ){
            ls.add(defaultFactory());
        }

        if( ls.size()>1 ){
            String order = System.getProperty("trambda.log.factory.order",null);
            if( order!=null && order.length()>0 ){
                String[] names = order.split(",");
                Map<LoggerFactory,Integer> matches = new LinkedHashMap<>();
                int nameIdx = -1;
                for( String name : names ){
                    nameIdx++;
                    for( var l : ls ){
                        if( l.getClass().getName().contains(name) ){
                            matches.put(l,nameIdx);
                        }
                    }
                }
                var existsOrder = new ArrayList<>(ls);
                ls.sort( (a,b) -> {
                    int ia = matches.getOrDefault(a,Integer.MIN_VALUE);
                    int ib = matches.getOrDefault(b,Integer.MIN_VALUE);
                    if( ia>=0 && ib>=0 && ia!=ib )return Integer.compare(ia,ib);
                    if( ia>=0 && ib<0 )return -1;
                    if( ib>=0 && ia<0 )return 1;
                    ia = existsOrder.indexOf(a);
                    ib = existsOrder.indexOf(b);
                    return Integer.compare(ia,ib);
                });
            }
        }

        factories = Collections.unmodifiableList(ls);
    }

    protected LoggerFactory defaultFactory(){
        return new LoggerFactory() {
            @Override
            public Logger getLogger(String name) {
                return new SimpleLogger(name);
            }
        };
    }

    protected Logger createLogger(String name){
        if( factories==null )return new SimpleLogger(name);

        var lgrs = new ArrayList<Logger>();
        for( var lf : factories ){
            var l = lf.getLogger(name);
            if( l!=null ){
                lgrs.add(l);
            }
        }

        if( lgrs.size()==1 )return lgrs.get(0);
        if( lgrs.isEmpty() )return new SimpleLogger(name);

        return new MultiLogger(lgrs).traceEnabled( ()->{
            for( var l : lgrs ){
                if( l==null )continue;
                if( l instanceof MultiLogger )continue;
                if( l instanceof SimpleLogger )continue;
                return l.isTraceEnabled();
            }
            return false;
        }).debugEnabled( ()->{
            for( var l : lgrs ){
                if( l==null )continue;
                if( l instanceof MultiLogger )continue;
                if( l instanceof SimpleLogger )continue;
                return l.isDebugEnabled();
            }
            return false;
        });
    }
}
