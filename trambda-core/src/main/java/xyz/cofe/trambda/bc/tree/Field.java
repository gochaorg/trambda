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

public class Field {
    protected final List<? extends ByteCode> byteCode;
    public Field(CField method, List<? extends ByteCode> byteCode, boolean copy ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( method==null )throw new IllegalArgumentException( "method==null" );
        this.byteCode = Collections.unmodifiableList(copy ? new ArrayList<>(byteCode) : byteCode);
        this.definition = method;
        this.body = byteCode.stream()
            .map( b -> b instanceof FieldByteCode ? (FieldByteCode)b : null )
            .filter( Objects::nonNull )
            .filter( b -> b.getFieldVisitorId()==method.getFieldVisitorId() )
            .collect( Collectors.toUnmodifiableList() );
    }

    //region definition : CField
    protected final CField definition;
    public CField getDefinition(){
        return definition;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected final List<FieldByteCode> body;
    public List<FieldByteCode> getBody(){ return body; }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public List<Annotation> getAnnotations(){
        if( annotations!=null )return annotations;
        annotations = body.stream().map( a -> a instanceof AnnotationDef ? (AnnotationDef<?>)a : null )
            .filter( Objects::nonNull )
            .map( a -> new Annotation(a,byteCode,false) )
            .collect(Collectors.toUnmodifiableList());
        return annotations;
    }
    //endregion
}
