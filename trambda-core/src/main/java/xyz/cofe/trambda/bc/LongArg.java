package xyz.cofe.trambda.bc;

/**
 * Аргумент bootstrap метода
 */
public class LongArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public LongArg(){}
    public LongArg(Long v){
        value = v;
    }

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
