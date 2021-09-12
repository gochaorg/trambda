package xyz.cofe.trambda.tcp;

import java.util.function.Consumer;

/**
 * Отписка от событий сервера
 *
 * <p>
 * см:
 * <ul>
 *     <li>{@link Subscribe}</li>
 *     <li>{@link SubscribeResult}</li>
 *     <li>{@link UnSubscribe}</li>
 *     <li>{@link TcpClient#subscribe(String, Consumer)}</li>
 *     <li>{@link TcpQuery#subscribe(Class, Consumer)}</li>
 * </ul>
 */
public class UnSubscribeResult implements Message {
    protected long subscribeTime;

    /**
     * Возвращает время начала подписки
     * @return время начала подписки
     */
    public long getSubscribeTime(){
        return subscribeTime;
    }

    /**
     * Указывает время начала подписки
     * @param subscribeTime время начала подписки
     */
    public void setSubscribeTime(long subscribeTime){
        this.subscribeTime = subscribeTime;
    }
}
