package xyz.cofe.trambda.bc.ann;

import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;

public class EmAArray extends EmbededAnnotation {
    private static final long serialVersionUID = 1;

    //region name : String
    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion

    public String toString(){
        return EmAArray.class.getSimpleName()+" name="+name;
    }

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public Eterable<ByteCode> nodes(){
        if( annotationByteCodes!=null )return Eterable.of(annotationByteCodes);
        return Eterable.empty();
    }
}
