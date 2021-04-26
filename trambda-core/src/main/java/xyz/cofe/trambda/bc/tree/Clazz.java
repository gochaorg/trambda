package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
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

public class Clazz {
    protected final List<? extends ByteCode> byteCode;

    public Clazz( List<? extends ByteCode> byteCode,  boolean copy ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        this.byteCode = Collections.unmodifiableList(
            copy ?  new ArrayList<>(byteCode) : byteCode
        );
    }

    //region definition : CBegin
    private CBegin definition;
    public CBegin getDefinition(){
        if( definition!=null )return definition;
        definition = byteCode.stream().filter( f -> f instanceof CBegin ).map( x -> (CBegin)x ).findFirst().orElse(null);
        return definition;
    }
    //endregion
    //region methods : List<Method>
    protected List<Method> methods;
    public List<Method> getMethods(){
        if( methods!=null )
            return methods;

        methods = byteCode.stream().map(
            bc -> bc instanceof CMethod ? (CMethod)bc : null
        ).filter( Objects::nonNull )
        .map( cm -> new Method(cm, byteCode, false) )
        .collect(Collectors.toUnmodifiableList());

        return methods;
    }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public List<Annotation> getAnnotations(){
        if( annotations!=null )
            return annotations;

        annotations = byteCode.stream().map(
            bc -> bc instanceof ClsByteCode && bc instanceof AnnotationDef ? (AnnotationDef<?>)bc : null
        ).filter( Objects::nonNull )
            .map( cm -> new Annotation(cm, byteCode, false) )
            .collect(Collectors.toUnmodifiableList());

        return annotations;
    }
    //endregion
    //region fields : List<Field>
    protected List<Field> fields;
    public List<Field> getFields(){
        if( fields!=null )
            return fields;

        fields = byteCode.stream().map(
            bc -> bc instanceof CField ? (CField)bc : null
        ).filter( Objects::nonNull )
        .map( cm -> new Field(cm, byteCode, false) )
        .collect(Collectors.toUnmodifiableList());

        return fields;
    }
    //endregion
}
