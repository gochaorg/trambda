package xyz.cofe.trambda.bc;

public class LongArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public LongArg(){}
    public LongArg(Long v){
        value = v;
    }

    private Long value;
    public Long getValue(){
        return value;
    }
    public void setValue(Long value){
        this.value = value;
    }

    public String toString(){
        return "LongArg{"+value+"}";
    }
}
