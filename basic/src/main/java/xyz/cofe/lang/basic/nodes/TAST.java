package xyz.cofe.lang.basic.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import xyz.cofe.stsl.types.Type;

/**
 * Типизированный узел
 * @param <R> ссылка на antlr узел/правило
 * @param <C> тип дочерних узлов
 */
public abstract class TAST<R extends ParserRuleContext,C extends AST<? extends ParserRuleContext, ?>> extends AST<R,C> {
    public TAST(R antlrRule) {
        super(antlrRule);
    }

    //region type : Type
    /**
     * Тип узла
     */
    protected Type type;

    /**
     * Возвращает тип узла
     * @return Тип узла
     */
    public Type getType() {
        return type;
    }

    /**
     * Указывает тип узла
     * @param type Тип узла
     */
    public void setType(Type type) {
        this.type = type;
    }
    //endregion
}
