package xyz.cofe.trambda.bc.bm;

import xyz.cofe.trambda.bc.bm.BootstrapMethArg;

/**
 * Аргумент bootstrap метода
 */
public class DoubleArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public DoubleArg(){}
    public DoubleArg(Double v){
        value = v;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public DoubleArg(DoubleArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        value = sample.getValue();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public DoubleArg clone(){ return new DoubleArg(this); }

    private Double value;
    public Double getValue(){
        return value;
    }
    public void setValue(Double value){
        this.value = value;
    }

    public String toString(){
        return "DoubleArg{"+value+"}";
    }
}
