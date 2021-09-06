package xyz.cofe.trambda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import xyz.cofe.collection.ImTree;
import xyz.cofe.collection.ImTreeWalk;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CMethod;

/**
 * Узел дерева лямд, описывает одну лямбду и связанные с ней лямбды.
 * <br>
 * Для вычисления этого узла см {@link LambdaDump}
 */
public class LambdaNode implements Serializable, ImTree<LambdaNode>, ImTreeWalk<LambdaNode> {
    /**
     * Конструктор по умолчанию
     */
    public LambdaNode(){
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public LambdaNode configure(Consumer<LambdaNode> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region clazz : CBegin - Байт-код класса содержащего лямбду
    /** Байт-код класса содержащего лямбду */
    protected CBegin clazz;

    /**
     * Возвращает байт-код класса содержащего лямбду
     * @return Байт-код класса содержащего лямбду
     */
    public CBegin getClazz(){
        return clazz;
    }

    /**
     * Указывает байт-код класса содержащего лямбду
     * @param clazz Байт-код класса содержащего лямбду
     */
    public void setClazz(CBegin clazz){
        this.clazz = clazz;
    }
    //endregion
    //region method : CMethod

    /**
     * Метод представляющий лямбду
     */
    protected CMethod method;

    /**
     * Возвращает метод представляющий лямбду
     * @return метод представляющий лямбду
     */
    public CMethod getMethod(){
        return method;
    }

    /**
     * Указывает метод представляющий лямбду
     * @param method метод представляющий лямбду
     */
    public void setMethod(CMethod method){
        this.method = method;
    }
    //endregion
    //region nodes : List<LambdaNode>

    /** Cписок лямбд которые вызываются из данной лямбды */
    protected List<LambdaNode> nodes;

    /**
     * Возвращает список лямбд которые вызываются из данной лямбды
     * @return список лямбд которые вызываются
     */
    public List<LambdaNode> getNodes(){
        if( nodes==null )nodes = new ArrayList<>();
        return nodes;
    }

    /**
     * Указывает список лямбд которые вызываются
     * @param nodes список лямбд которые вызываются
     */
    public void setNodes(List<LambdaNode> nodes){
        this.nodes = nodes;
    }
    //endregion

    //region toString()
    public String toString(){
        return toString(0);
    }
    private String toString( int level ){
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(level);
        sb.append(indent);
        sb.append(LambdaNode.class.getSimpleName());
        if( clazz!=null ){
            sb.append(" ");
            sb.append(clazz.getName());
        }
        if( method!=null ){
            sb.append(" ");
            sb.append(method);
        }
        if( nodes!=null ){
            sb.append(System.lineSeparator());
            for( var n : nodes ){
                String s = n.toString(level+1);
                sb.append(s);
                if( n.nodes==null || n.nodes.isEmpty() ){
                    sb.append(System.lineSeparator());
                }
            }
        }
        return sb.toString();
    }
    //endregion

    //region nodes : Eterable<LambdaNode>
    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public Eterable<LambdaNode> nodes(){
        return Eterable.of(getNodes());
    }
    //endregion
}
