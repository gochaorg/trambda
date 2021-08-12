package xyz.cofe.trambda.log.api.impl;

import xyz.cofe.trambda.log.api.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiLogger implements Logger {
    public final Logger[] loggers;

    public MultiLogger(Iterable<Logger> loggers){
        if( loggers==null )throw new IllegalArgumentException( "loggers==null" );
        List<Logger> logrs = new ArrayList<>();
        for( var l : loggers ){
            if( l!=null )logrs.add(l);
        }
        this.loggers = logrs.toArray(new Logger[0]);
    }

    public MultiLogger(Logger[] loggers){
        if( loggers==null )throw new IllegalArgumentException( "loggers==null" );
        this.loggers = loggers;
    }

    @Override
    public void error(String message) {
        for( var log : loggers ){
            if( log!=null )log.error(message);
        }
    }

    @Override
    public void error(String message, Object... args) {
        for( var log : loggers ){
            if( log!=null )log.error(message,args);
        }
    }

    @Override
    public void error(String message, Throwable err) {
        for( var log : loggers ){
            if( log!=null )log.error(message,err);
        }
    }

    @Override
    public void warn(String message) {
        for( var log : loggers ){
            if( log!=null )log.warn(message);
        }
    }

    @Override
    public void warn(String message, Object... args) {
        for( var log : loggers ){
            if( log!=null )log.warn(message,args);
        }
    }

    @Override
    public void warn(String message, Throwable err) {
        for( var log : loggers ){
            if( log!=null )log.warn(message,err);
        }
    }

    @Override
    public void info(String message) {
        for( var log : loggers ){
            if( log!=null )log.info(message);
        }
    }

    @Override
    public void info(String message, Object... args) {
        for( var log : loggers ){
            if( log!=null )log.info(message,args);
        }
    }

    @Override
    public void info(String message, Throwable err) {
        for( var log : loggers ){
            if( log!=null )log.info(message,err);
        }
    }

    @Override
    public void debug(String message) {
        for( var log : loggers ){
            if( log!=null )log.debug(message);
        }
    }

    @Override
    public void debug(String message, Object... args) {
        for( var log : loggers ){
            if( log!=null )log.debug(message,args);
        }
    }

    @Override
    public void debug(String message, Throwable err) {
        for( var log : loggers ){
            if( log!=null )log.debug(message,err);
        }
    }

    @Override
    public void trace(String message) {
        for( var log : loggers ){
            if( log!=null )log.trace(message);
        }
    }

    @Override
    public void trace(String message, Object... args) {
        for( var log : loggers ){
            if( log!=null )log.trace(message,args);
        }
    }

    @Override
    public void trace(String message, Throwable err) {
        for( var log : loggers ){
            if( log!=null )log.info(message,err);
        }
    }

    protected Supplier<Boolean> traceEnabled;
    public Supplier<Boolean> traceEnabled(){ return traceEnabled; }
    public MultiLogger traceEnabled(Supplier<Boolean> t){ traceEnabled = t; return this; }

    @Override
    public boolean isTraceEnabled() {
        var te = traceEnabled();
        return te!=null ? te.get() : false;
    }

    protected Supplier<Boolean> debugEnabled;
    public Supplier<Boolean> debugEnabled(){ return debugEnabled; }
    public MultiLogger debugEnabled(Supplier<Boolean> t){ debugEnabled = t; return this; }

    @Override
    public boolean isDebugEnabled() {
        var te = debugEnabled();
        return te!=null ? te.get() : false;
    }
}
