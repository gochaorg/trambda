package xyz.cofe.trambda.bc.bm;

import xyz.cofe.trambda.bc.bm.BootstrapMethArg;

/**
 * Аргумент bootstrap метода
 */
public class FloatArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public FloatArg(){}
    public FloatArg(Float v){
        value = v;
    }

    private Float value;
    public Float getValue(){
        return value;
    }
    public void setValue(Float value){
        this.value = value;
    }

    public String toString(){
        return "FloatArg{"+value+"}";
    }
}