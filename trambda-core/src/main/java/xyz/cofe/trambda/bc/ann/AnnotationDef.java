package xyz.cofe.trambda.bc.ann;

import xyz.cofe.trambda.bc.ByteCode;

public interface AnnotationDef<C extends ByteCode> extends AnnVisIdProperty {
    public default int getAnnotationDefVisitorId(){ return getAnnotationVisitorId(); }
}
