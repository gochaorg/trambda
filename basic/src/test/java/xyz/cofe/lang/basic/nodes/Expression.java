package xyz.cofe.lang.basic.nodes;

/**
 * Выражение
 */
public interface Expression {
    /**
     * Вычисление выражения
     * @return значение
     */
    Object eval();
}
