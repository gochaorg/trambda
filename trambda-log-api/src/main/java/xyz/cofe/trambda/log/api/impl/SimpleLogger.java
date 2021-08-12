package xyz.cofe.trambda.log.api.impl;

import xyz.cofe.trambda.log.api.Logger;

import java.util.List;
import java.util.Set;

public class SimpleLogger implements Logger {
    public final String name;
    public SimpleLogger(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        this.name = name;
    }

    private void prefixMessage(String prefix){
        System.out.print(prefix);
        System.out.print(" [");
        System.out.print(name);
        System.out.print("] ");
    }

    private void message(String prefix,String message) {
        prefixMessage(prefix);
        System.out.println(message);
    }

    private void message(String prefix,String message, Object... args) {
        prefixMessage(prefix);
        System.out.print(message);
        if( args!=null ){
            for( var a : args ){
                System.out.print(a);
            }
        }
        System.out.println();
    }

    private void message(String prefix,String message, Throwable err) {
        prefixMessage(prefix);
        System.out.print(message);
        System.out.println(err);
    }

    @Override public void error(String message) { message("ERROR ",message); }
    @Override public void error(String message, Object... args) { message("ERROR ",message,args); }
    @Override public void error(String message, Throwable err) { message("ERROR ",message,err); }

    @Override public void warn(String message) { message("WARN ",message); }
    @Override public void warn(String message, Object... args) { message("WARN ",message,args); }
    @Override public void warn(String message, Throwable err) { message("WARN ",message,err); }

    @Override public void info(String message) { message("INFO ",message); }
    @Override public void info(String message, Object... args) { message("INFO ",message,args); }
    @Override public void info(String message, Throwable err) { message("INFO ",message,err); }

    @Override public void debug(String message) { message("DEBUG ",message); }
    @Override public void debug(String message, Object... args) { message("DEBUG ",message,args); }
    @Override public void debug(String message, Throwable err) { message("DEBUG ",message,err); }

    @Override public void trace(String message) { message("TRACE ",message); }
    @Override public void trace(String message, Object... args) { message("TRACE ",message,args); }
    @Override public void trace(String message, Throwable err) { message("TRACE ",message,err); }

    private static final Set<String> traceEnableLevel = Set.of("trace");
    private static final Set<String> debugEnableLevel = Set.of("trace","debug");
    private static final Set<String> infoEnableLevel = Set.of("trace","debug","info");
    private static final Set<String> warnEnableLevel = Set.of("trace","debug","info","warn");
    private static final Set<String> errorEnableLevel = Set.of("trace","debug","info","warn","error");
    private static final String defaultLevel = "debug";

    @Override
    public boolean isTraceEnabled() {
        var lvl = System.getProperty("trambda.log."+name+".level",defaultLevel);
        return traceEnableLevel.contains(lvl);
    }

    @Override
    public boolean isDebugEnabled() {
        var lvl = System.getProperty("trambda.log."+name+".level",defaultLevel);
        return debugEnableLevel.contains(lvl);
    }
}
