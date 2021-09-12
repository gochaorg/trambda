package xyz.cofe.trambda.tcp;

/**
 * Подписчик на события сервера/сессии {@link TcpServer}
 */
public interface TrListener {
    /**
     * Уведомление о событии сервера/сессии
     * @param ev событие
     */
    void trEvent(TrEvent ev);
}
