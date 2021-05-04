package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.Tuple2;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.ann.EmbededAnnotation;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.cls.ClsByteCode;
import xyz.cofe.trambda.bc.fld.FieldByteCode;
import xyz.cofe.trambda.bc.mth.MethodByteCode;

public class Clazz implements GetAnnotations, GetDefinition {
    public Clazz( List<? extends ByteCode> byteCode,  boolean copy ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );

        definition = byteCode.stream().filter(f -> f instanceof CBegin).map(x -> (CBegin) x).findFirst().orElse(null);

        methods = byteCode.stream().map(
            bc -> bc instanceof CMethod ? (CMethod)bc : null
        ).filter( Objects::nonNull )
            .map( cm -> new Method(cm, byteCode) )
            .collect(Collectors.toList());

        annotations = byteCode.stream().map(
            bc -> bc instanceof ClsByteCode && bc instanceof AnnotationDef ? (AnnotationDef)bc : null
        ).filter( Objects::nonNull )
            .map( cm -> new Annotation(cm, byteCode) )
            .collect(Collectors.toList());

        fields = byteCode.stream().map(
            bc -> bc instanceof CField ? (CField)bc : null
        ).filter( Objects::nonNull )
            .map( cm -> new Field(cm, byteCode) )
            .collect(Collectors.toList());
    }

    //region definition : CBegin
    private CBegin definition;
    public synchronized CBegin getDefinition(){
        return definition;
    }
    public synchronized void setDefinition(CBegin begin){
        this.definition = begin;
    }
    //endregion
    //region methods : List<Method>
    protected List<Method> methods;
    public synchronized List<Method> getMethods(){
        if( methods==null )methods = new ArrayList<>();
        return methods;
    }
    public synchronized void setMethods(List<Method> methods){
        this.methods = methods;
    }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public synchronized List<Annotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }
    public synchronized void setAnnotations(List<Annotation> annotations){
        this.annotations = annotations;
    }
    //endregion
    //region fields : List<Field>
    protected List<Field> fields;
    public synchronized List<Field> getFields(){
        if( fields==null )fields = new ArrayList<>();
        return fields;
    }
    public synchronized void setFields(List<Field> fields){
        this.fields = fields;
    }
    //endregion

    public synchronized byte[] toByteCode(){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        write(cw);
        return cw.toByteArray();
    }

    public synchronized void write(ClassWriter cw){
        if( cw==null )throw new IllegalArgumentException( "cw==null" );
    }
}
