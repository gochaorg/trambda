package xyz.cofe.trambda.bc.ann;

public class EmAArray extends EmbededAnnotation {
    private static final long serialVersionUID = 1;

    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String toString(){
        return EmAArray.class.getSimpleName()+" name="+name;
    }
}
