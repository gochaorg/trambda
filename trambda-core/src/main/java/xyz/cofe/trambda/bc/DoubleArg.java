package xyz.cofe.trambda.bc;

/**
 * Аргумент bootstrap метода
 */
public class DoubleArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public DoubleArg(){}
    public DoubleArg(Double v){
        value = v;
    }

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
