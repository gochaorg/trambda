package xyz.cofe.lang.basic.nodes;

/**
 * Унарная операция
 */
public interface UnaryExpression extends Expression, Operator {
    Expression expr();
}
