package xyz.cofe.trambda.bc;

/**
 * Аргумент bootstrap метода
 */
public class IntArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public IntArg(){}
    public IntArg(Integer v){
        value = v;
    }

    private Integer value;

    public Integer getValue(){
        return value;
    }

    public void setValue(Integer value){
        this.value = value;
    }

    public String toString(){
        return "IntArg{"+value+"}";
    }
}
