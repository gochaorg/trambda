package xyz.cofe.lang.basic.nodes;

/**
 * Литеральное значение
 */
public interface Literal {
    /**
     * Непосредственно значение
     * @return значение
     */
    Object value();

    /**
     * Тип значения
     * @return тип значения
     */
    LiteralKind kind();
}
