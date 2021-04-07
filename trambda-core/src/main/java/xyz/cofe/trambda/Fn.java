package xyz.cofe.trambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Лямбда передаваемая на сервер
 * @param <A> Тип сервиса который доступен ра сервере
 * @param <Z> Тип результата возвращаемый с сервера
 */
public interface Fn<A,Z> extends Serializable, Function<A,Z> {
    /**
     * Вызов лямбды
     * @param a сервис передаваемый в лямбду
     * @return результата возвращаемый с сервера
     */
    public Z apply(A a);
}
