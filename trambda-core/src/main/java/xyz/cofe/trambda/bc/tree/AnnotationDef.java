package xyz.cofe.trambda.bc.tree;

import xyz.cofe.trambda.bc.ByteCode;

public class AnnotationDef<DEF extends ByteCode> implements Annotation<DEF> {
    public AnnotationDef(DEF definition){
        this.definition = definition;
    }

    protected DEF definition;

    public DEF getDefinition(){
        return definition;
    }
}
