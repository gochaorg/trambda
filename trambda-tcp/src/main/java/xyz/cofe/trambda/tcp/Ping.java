package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.tcp.Message;

/**
 * Пинг запрос, ожидается ответ на него {@link Pong}
 */
public class Ping implements Message {
    private static final long serialVersionUID = 4L;
}
