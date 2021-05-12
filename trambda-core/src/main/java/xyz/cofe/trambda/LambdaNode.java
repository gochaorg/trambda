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

public class LambdaNode implements Serializable, ImTree<LambdaNode>, ImTreeWalk<LambdaNode> {
    public LambdaNode(){
    }

    public LambdaNode configure(Consumer<LambdaNode> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region clazz : CBegin
    protected CBegin clazz;
    public CBegin getClazz(){
        return clazz;
    }
    public void setClazz(CBegin clazz){
        this.clazz = clazz;
    }
    //endregion
    //region method : CMethod
    protected CMethod method;
    public CMethod getMethod(){
        return method;
    }
    public void setMethod(CMethod method){
        this.method = method;
    }
    //endregion
    //region nodes : List<LambdaNode>
    protected List<LambdaNode> nodes;
    public List<LambdaNode> getNodes(){
        if( nodes==null )nodes = new ArrayList<>();
        return nodes;
    }
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
