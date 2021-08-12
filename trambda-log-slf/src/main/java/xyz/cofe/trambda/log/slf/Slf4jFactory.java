package xyz.cofe.trambda.log.slf;

import org.slf4j.LoggerFactory;
import xyz.cofe.trambda.log.api.Logger;

public class Slf4jFactory implements xyz.cofe.trambda.log.api.spi.LoggerFactory {
    @Override
    public Logger getLogger(String name) {
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return proxy(LoggerFactory.getLogger(name));
    }

    protected Logger proxy( org.slf4j.Logger logger ){
        return new Logger() {
            @Override
            public void error(String message) {
                logger.error(message);
            }

            @Override
            public void error(String message, Object... args) {
                logger.error(message,args);
            }

            @Override
            public void error(String message, Throwable err) {
                logger.error(message,err);
            }

            @Override
            public void warn(String message) {
                logger.warn(message);
            }

            @Override
            public void warn(String message, Object... args) {
                logger.warn(message,args);
            }

            @Override
            public void warn(String message, Throwable err) {
                logger.warn(message,err);
            }

            @Override
            public void info(String message) {
                logger.info(message);
            }

            @Override
            public void info(String message, Object... args) {
                logger.info(message, args);
            }

            @Override
            public void info(String message, Throwable err) {
                logger.info(message,err);
            }

            @Override
            public void debug(String message) {
                logger.debug(message);
            }

            @Override
            public void debug(String message, Object... args) {
                logger.debug(message, args);
            }

            @Override
            public void debug(String message, Throwable err) {
                logger.debug(message, err);
            }

            @Override
            public void trace(String message) {
                logger.trace(message);
            }

            @Override
            public void trace(String message, Object... args) {
                logger.trace(message, args);
            }

            @Override
            public void trace(String message, Throwable err) {
                logger.trace(message, err);
            }

            @Override
            public boolean isTraceEnabled() {
                return logger.isTraceEnabled();
            }

            @Override
            public boolean isDebugEnabled() {
                return logger.isDebugEnabled();
            }
        };
    }
}
