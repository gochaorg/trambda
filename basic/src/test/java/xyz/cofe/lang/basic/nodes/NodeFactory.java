package xyz.cofe.lang.basic.nodes;

import xyz.cofe.lang.basic.nodes.Expression;

/**
 * Фабрика узлов AST
 */
public class NodeFactory {
    /**
     * Литеральное значение
     * @return выражение
     */
    public Expression literal(){
        return null;
    }

    public Expression unary(String op, Expression e){
        return null;
    }

    public Expression binary(String op,Expression left,Expression right){
        return null;
    }
}
