package xyz.cofe.trambda.bc.ann;

public class EmANameDesc extends EmbededAnnotation {
    private static final long serialVersionUID = 1;

    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    protected String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }

    public String toString(){
        return EmANameDesc.class.getSimpleName()+" name="+name+" descriptor="+descriptor;
    }
}
