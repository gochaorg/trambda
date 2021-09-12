package xyz.cofe.trambda.tcp;

/**
 * Результат подписки (успешное) на событие сервера
 */
public class SubscribeResult implements Message {
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
