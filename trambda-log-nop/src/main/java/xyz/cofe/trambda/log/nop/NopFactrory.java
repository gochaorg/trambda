package xyz.cofe.trambda.log.nop;

import xyz.cofe.trambda.log.api.Logger;
import xyz.cofe.trambda.log.api.spi.LoggerFactory;

public class NopFactrory implements LoggerFactory {
    @Override
    public Logger getLogger(String name) {
        return new Logger() {
            @Override
            public void error(String message) {
            }

            @Override
            public void error(String message, Object... args) {

            }

            @Override
            public void error(String message, Throwable err) {

            }

            @Override
            public void warn(String message) {

            }

            @Override
            public void warn(String message, Object... args) {

            }

            @Override
            public void warn(String message, Throwable err) {

            }

            @Override
            public void info(String message) {

            }

            @Override
            public void info(String message, Object... args) {

            }

            @Override
            public void info(String message, Throwable err) {

            }

            @Override
            public void debug(String message) {

            }

            @Override
            public void debug(String message, Object... args) {

            }

            @Override
            public void debug(String message, Throwable err) {

            }

            @Override
            public void trace(String message) {

            }

            @Override
            public void trace(String message, Object... args) {

            }

            @Override
            public void trace(String message, Throwable err) {

            }

            @Override
            public boolean isTraceEnabled() {
                return false;
            }

            @Override
            public boolean isDebugEnabled() {
                return false;
            }
        };
    }
}
