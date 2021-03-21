package xyz.cofe.trambda.bc;

public class TypeArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public TypeArg(){}
    public TypeArg(String type){
        this.type = type;
    }

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
