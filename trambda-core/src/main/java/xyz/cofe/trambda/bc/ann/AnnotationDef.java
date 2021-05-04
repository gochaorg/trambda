package xyz.cofe.trambda.bc.ann;

public interface AnnotationDef extends AnnVisIdProperty {
    public default int getAnnotationDefVisitorId(){ return getAnnotationVisitorId(); }
}
