package xyz.cofe.trambda.sec;

/**
 * Поддержка клонирования объекта
 * @param <T> тип клона
 */
public interface CloneSupport<T> {
    /**
     * Клонирование объекта
     * @return объект
     */
    T clone();
}
