package xyz.cofe.trambda;

/**
 * Общий интерфейс для вызова лямбды на сервере
 * @param <ENV> Сервис предоставляемый на сервере
 */
public interface Query<ENV> {
    /**
     * Вызов лямбды на сервере
     * @param fn лямбда
     * @param <RES> Сервис предоставляемый на сервере
     * @return результат вычисления на сервере
     */
    public <RES> RES apply( Fn<ENV,RES> fn );
}
