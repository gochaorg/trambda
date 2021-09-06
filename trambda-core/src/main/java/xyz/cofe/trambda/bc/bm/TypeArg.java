package xyz.cofe.trambda.bc.bm;

import xyz.cofe.trambda.bc.bm.BootstrapMethArg;

/**
 * Аргумент bootstrap метода
 */
public class TypeArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public TypeArg(){}
    public TypeArg(String type){
        this.type = type;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public TypeArg(TypeArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        type = sample.getType();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TypeArg clone(){ return new TypeArg(this); }

    private String type;
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String toString(){
        return "Type "+type;
    }
}
