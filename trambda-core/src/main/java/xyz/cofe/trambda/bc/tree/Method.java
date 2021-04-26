package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.fld.FieldByteCode;
import xyz.cofe.trambda.bc.mth.MethodByteCode;

public class Method {
    protected final List<? extends ByteCode> byteCode;
    public Method( CMethod method, List<? extends ByteCode> byteCode, boolean copy ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( method==null )throw new IllegalArgumentException( "method==null" );
        this.byteCode = Collections.unmodifiableList(copy ? new ArrayList<>(byteCode) : byteCode);
        this.definition = method;
        body = byteCode.stream()
            .map( b -> b instanceof MethodByteCode ? (MethodByteCode)b : null)
            .filter( Objects::nonNull )
            .filter( b -> b.getMethodVisitorId()==method.getMethodVisitorId() )
            .collect(Collectors.toUnmodifiableList());
    }

    //region definition : CMethod
    protected final CMethod definition;
    public CMethod getDefinition(){
        return definition;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected final List<MethodByteCode> body;
    public List<MethodByteCode> getBody(){ return body; }
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
