package xyz.cofe.trambda.bc.tree;

import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.fld.FAnnotation;
import xyz.cofe.trambda.bc.fld.FTypeAnnotation;
import xyz.cofe.trambda.bc.mth.MAnnotation;
import xyz.cofe.trambda.bc.mth.MAnnotationDefault;
import xyz.cofe.trambda.bc.mth.MInsnAnnotation;
import xyz.cofe.trambda.bc.mth.MLocalVariableAnnotation;
import xyz.cofe.trambda.bc.mth.MParameterAnnotation;
import xyz.cofe.trambda.bc.mth.MTryCatchAnnotation;
import xyz.cofe.trambda.bc.mth.MTypeAnnotation;

public interface Annotation<DEF extends ByteCode> {
    public DEF getDefinition();

    public static Annotation<CAnnotation> create(CAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<CTypeAnnotation> create(CTypeAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<FAnnotation> create(FAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<FTypeAnnotation> create(FTypeAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MAnnotationDefault> create(MAnnotationDefault ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MAnnotation> create(MAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MTypeAnnotation> create(MTypeAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MParameterAnnotation> create(MParameterAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MInsnAnnotation> create(MInsnAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MTryCatchAnnotation> create(MTryCatchAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<MLocalVariableAnnotation> create(MLocalVariableAnnotation ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<EmANameDesc> create(EmANameDesc ann){
        return new AnnotationDef<>(ann);
    }
    public static Annotation<EmAArray> create(EmAArray ann){
        return new AnnotationDef<>(ann);
    }
}
