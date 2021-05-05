package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.fld.FAnnotation;
import xyz.cofe.trambda.bc.fld.FTypeAnnotation;
import xyz.cofe.trambda.bc.fld.FieldByteCode;
import xyz.cofe.trambda.bc.fld.FieldEnd;

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
            .map( a -> new Annotation(a,byteCode).annotationDefVisitorId(a.getAnnotationDefVisitorId()) )
            .collect(Collectors.toUnmodifiableList());
    }

    //region definition : CField
    protected CField definition;
    public synchronized CField getDefinition(){
        return definition;
    }
    public synchronized void setDefinition(CField field){
        definition = field;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected List<FieldByteCode> body;
    public synchronized List<FieldByteCode> getBody(){
        if( body==null )body = new ArrayList<>();
        return body;
    }
    public synchronized void setBody( List<FieldByteCode> body ){
        this.body = body;
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

    public synchronized void write(ClassWriter cw){
        if( cw==null )throw new IllegalArgumentException( "cw==null" );
        if( definition==null )throw new IllegalStateException("definition==null");
        var v = cw.visitField(definition.getAccess(),definition.getName(),definition.getDescriptor(),definition.getSignature(),definition.getValue());
        if( body!=null ){
            for( var b : body ){
                if( b instanceof FieldEnd )write(v,(FieldEnd) b);
                else if( b instanceof FTypeAnnotation )write(v,(FTypeAnnotation) b);
                else if( b instanceof FAnnotation )write(v,(FAnnotation) b);
            }
        }
    }

    protected void write(FieldVisitor v, FieldEnd e){
        v.visitEnd();
    }

    protected void write(FieldVisitor v, FTypeAnnotation e){
        var av = v.visitTypeAnnotation(e.getTypeRef(), e.getTypePath()!=null ? TypePath.fromString(e.getTypePath()) : null,e.getDescriptor(), e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }

    protected void write(FieldVisitor v, FAnnotation e){
        var av = v.visitAnnotation(e.getDescriptor(),e.isVisible());
        getAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == e.getAnnotationDefVisitorId()
        ).findFirst().ifPresent( nested -> {
            nested.write(av,nested.getBody());
        });
    }
}