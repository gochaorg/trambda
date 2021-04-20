package xyz.cofe.trambda.bc.ann;

public class EmAnnArray extends EmbededAnnotation {
    private static final long serialVersionUID = 1;

    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String toString(){
        return EmAnnArray.class.getSimpleName()+" name="+name;
    }
}
