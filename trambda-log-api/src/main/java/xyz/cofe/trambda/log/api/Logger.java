package xyz.cofe.trambda.log.api;

import xyz.cofe.trambda.log.api.spi.LoggerRegistry;

public interface Logger {
    void error(String message);
    void error(String message,Object ... args);
    void error(String message, Throwable err);
    void warn(String message);
    void warn(String message,Object ... args);
    void warn(String message, Throwable err);
    void info(String message);
    void info(String message,Object ... args);
    void info(String message, Throwable err);
    void debug(String message);
    void debug(String message,Object ... args);
    void debug(String message, Throwable err);
    void trace(String message);
    void trace(String message,Object ... args);
    void trace(String message, Throwable err);

    boolean isTraceEnabled();
    boolean isDebugEnabled();

    public static Logger of(Class<?> cls){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        return LoggerRegistry.getInstance().getLogger(cls.getName());
    }
}
