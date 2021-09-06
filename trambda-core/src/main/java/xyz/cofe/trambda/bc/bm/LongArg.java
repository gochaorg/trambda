package xyz.cofe.trambda.bc.bm;

import xyz.cofe.trambda.bc.bm.BootstrapMethArg;

/**
 * Аргумент bootstrap метода
 */
public class LongArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public LongArg(){}
    public LongArg(Long v){
        value = v;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public LongArg(LongArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        value = sample.getValue();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public LongArg clone(){ return new LongArg(this); }

    //region value : Long
    private Long value;
    public Long getValue(){
        return value;
    }
    public void setValue(Long value){
        this.value = value;
    }
    //endregion

    public String toString(){
        return "LongArg{"+value+"}";
    }
}
