package xyz.cofe.trambda;

import xyz.cofe.fn.Fn1;

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
    public <RES> RES apply( Fn1<ENV,RES> fn );
}
