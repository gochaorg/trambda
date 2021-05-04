package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.fld.FieldByteCode;

public class Field implements GetAnnotations, GetDefinition {
    public Field(CField method, List<? extends ByteCode> byteCode ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( method==null )throw new IllegalArgumentException( "method==null" );

        this.definition = method;

        this.body = byteCode.stream()
            .map( b -> b instanceof FieldByteCode ? (FieldByteCode)b : null )
            .filter( Objects::nonNull )
            .filter( b -> b.getFieldVisitorId()==method.getFieldVisitorId() )
            .collect( Collectors.toUnmodifiableList() );

        annotations = body.stream().map( a -> a instanceof AnnotationDef ? (AnnotationDef)a : null )
            .filter( Objects::nonNull )
            .map( a -> new Annotation(a,byteCode) )
            .collect(Collectors.toUnmodifiableList());
    }

    //region definition : CField
    protected CField definition;
    public CField getDefinition(){
        return definition;
    }
    public void setDefinition(CField field){
        definition = field;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected List<FieldByteCode> body;
    public List<FieldByteCode> getBody(){
        if( body==null )body = new ArrayList<>();
        return body;
    }
    public void setBody( List<FieldByteCode> body ){
        this.body = body;
    }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public List<Annotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }
    public void setAnnotations(List<Annotation> annotations){
        this.annotations = annotations;
    }
    //endregion
}
