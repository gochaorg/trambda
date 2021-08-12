package xyz.cofe.lang.basic.nodes;

/**
 * Бинарные выражение
 */
public interface BinaryExpression extends Expression, Operator {
    /**
     * Левое выражение
     * @return выражение
     */
    Expression left();

    /**
     * Правое выражение
     * @return выражение
     */
    Expression right();
}
