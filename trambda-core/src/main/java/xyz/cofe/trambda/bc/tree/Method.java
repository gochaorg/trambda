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

public class Method implements GetAnnotations, GetDefinition {
    public Method( CMethod method, List<? extends ByteCode> byteCode ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( method==null )throw new IllegalArgumentException( "method==null" );

        this.definition = method;

        body = byteCode.stream()
            .map( b -> b instanceof MethodByteCode ? (MethodByteCode)b : null)
            .filter( Objects::nonNull )
            .filter( b -> b.getMethodVisitorId()==method.getMethodVisitorId() )
            .collect(Collectors.toUnmodifiableList());

        annotations = body.stream().map( a -> a instanceof AnnotationDef ? (AnnotationDef)a : null )
            .filter( Objects::nonNull )
            .map( a -> new Annotation(a,byteCode) )
            .collect(Collectors.toUnmodifiableList());
    }

    //region definition : CMethod
    protected CMethod definition;
    public CMethod getDefinition(){
        return definition;
    }
    public void setDefinition(CMethod definition){
        this.definition = definition;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected List<MethodByteCode> body;
    public List<MethodByteCode> getBody(){
        if( body==null )body = new ArrayList<>();
        return body;
    }
    public void setBody(List<MethodByteCode> body){
        this.body = body;
    }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public List<Annotation> getAnnotations(){
        if( annotations!=null ){
            annotations = new ArrayList<>();
        }
        return annotations;
    }
    public void setAnnotations(List<Annotation> annotations){
        this.annotations = annotations;
    }
    //endregion
}
