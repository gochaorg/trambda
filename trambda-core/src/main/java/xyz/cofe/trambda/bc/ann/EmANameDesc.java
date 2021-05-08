package xyz.cofe.trambda.bc.ann;

import java.util.ArrayList;
import java.util.List;

public class EmANameDesc extends EmbededAnnotation {
    private static final long serialVersionUID = 1;

    //region name : String
    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String
    protected String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion

    public String toString(){
        return EmANameDesc.class.getSimpleName()+" name="+name+" descriptor="+descriptor;
    }
}
