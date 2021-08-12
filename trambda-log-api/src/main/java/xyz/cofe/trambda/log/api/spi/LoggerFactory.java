package xyz.cofe.trambda.log.api.spi;

import xyz.cofe.trambda.log.api.Logger;

public interface LoggerFactory {
    Logger getLogger(String name);
}
