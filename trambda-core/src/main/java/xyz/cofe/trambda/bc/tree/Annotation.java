package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.TypePath;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AEnd;
import xyz.cofe.trambda.bc.ann.AEnum;
import xyz.cofe.trambda.bc.ann.APair;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.cls.ClsByteCode;
import xyz.cofe.trambda.bc.fld.FAnnotation;
import xyz.cofe.trambda.bc.fld.FTypeAnnotation;
import xyz.cofe.trambda.bc.mth.MAnnotation;
import xyz.cofe.trambda.bc.mth.MAnnotationDefault;

public class Annotation {
    public Annotation(AnnotationDef definition, List<? extends ByteCode> byteCode ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( definition==null )throw new IllegalArgumentException( "definition==null" );

        this.definition = definition;

        body = byteCode.stream()
            .map( a -> a instanceof AnnotationByteCode ? (AnnotationByteCode)a : null )
            .filter( Objects::nonNull )
            .filter( a -> a.getAnnotationVisitorId() == definition.getAnnotationDefVisitorId() )
            .collect(Collectors.toUnmodifiableList());

        nestedAnnotations = body.stream()
            .map( a -> a instanceof AnnotationDef ? (AnnotationDef)a : null )
            .filter(Objects::nonNull)
            .map( a -> new Annotation(a,byteCode).annotationDefVisitorId(a.getAnnotationDefVisitorId()) )
            .collect(Collectors.toUnmodifiableList());
    }

    protected int annotationDefVisitorId = -1;
    public synchronized int getAnnotationDefVisitorId(){ return annotationDefVisitorId; }
    public synchronized void setAnnotationDefVisitorId(int v){annotationDefVisitorId = v;}
    public synchronized Annotation annotationDefVisitorId(int v){
        setAnnotationDefVisitorId(v);
        return this;
    }

    //region definition : AnnotationDef<? extends ByteCode>
    protected AnnotationDef definition;
    public synchronized AnnotationDef getDefinition(){
        return definition;
    }
    public synchronized void setDefinition(AnnotationDef annotationDef){
        this.definition = annotationDef;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected List<AnnotationByteCode> body;
    public synchronized List<AnnotationByteCode> getBody(){
        if( body==null )body = new ArrayList<>();
        return body;
    }
    public synchronized void setBody(List<AnnotationByteCode> body){
        this.body = body;
    }
    //endregion
    //region nestedAnnotation : List<Annotation>
    protected List<Annotation> nestedAnnotations;
    public synchronized List<Annotation> getNestedAnnotations(){
        if( nestedAnnotations==null )nestedAnnotations = new ArrayList<>();
        return nestedAnnotations;
    }
    public synchronized void setNestedAnnotations(List<Annotation> nestedAnnotations){
        this.nestedAnnotations = nestedAnnotations;
    }
    //endregion

    //region visit()
    public synchronized void visit(BiConsumer<Annotation,List<Annotation>> path){
        if( path==null )throw new IllegalArgumentException( "path==null" );
        List<List<Annotation>> workSet = new LinkedList<>();

        List<Annotation> start = new LinkedList<>();

        start.add(this);
        workSet.add(start);

        while( !workSet.isEmpty() ){
            var curPath = workSet.remove(0);
            var curNode = curPath.get(curPath.size()-1);
            var nextNodes = curNode.getNestedAnnotations();
            if( nextNodes!=null && !nextNodes.isEmpty() ){
                int idx = 0;
                for( var nextNode : nextNodes ){
                    if( nextNode==null )continue;

                    var nextPath = new ArrayList<>(curPath);
                    nextPath.add(nextNode);

                    workSet.add(idx,nextPath);
                    idx++;
                }
            }
            path.accept(curNode,curPath);
        }
    }
    //endregion

    public synchronized void write(ClassWriter cw){
        if( cw==null )throw new IllegalArgumentException( "cw==null" );
        if( definition==null )throw new IllegalStateException("definition==null");
        if( !(definition instanceof ClsByteCode) ){
            throw new IllegalStateException("definition not instance of ClsByteCode");
        }
        if( definition instanceof CAnnotation ){
            write(cw,(CAnnotation) definition);
        }else if( definition instanceof CTypeAnnotation ){
            write(cw,(CTypeAnnotation) definition);
        }else{
            throw new UnsupportedOperationException("can't write "+definition);
        }
    }

    protected void write(ClassWriter cw, CAnnotation ann){
        var vis = cw.visitAnnotation(ann.getDescriptor(), ann.isVisible());
        write(vis,body);
    }

    protected void write(ClassWriter cw, CTypeAnnotation ann){
        var vis = cw.visitTypeAnnotation(
            ann.getTypeRef(),
            ann.getTypePath()!=null ? TypePath.fromString(ann.getTypePath()) : null,
            ann.getDescriptor(),
            ann.isVisible()
        );
        write(vis,body);
    }

    public void write( AnnotationVisitor vis, List<AnnotationByteCode> body ){
        if( vis==null )throw new IllegalArgumentException( "vis==null" );
        if( body!=null ){
            for( var b : body ){
                if( b instanceof AEnd )write(vis,(AEnd) b);
                else if( b instanceof AEnum )write(vis,(AEnum) b);
                else if( b instanceof EmAArray )write(vis,(EmAArray) b);
                else if( b instanceof EmANameDesc )write(vis,(EmANameDesc) b);
                else if( b instanceof APair.APairBoolean )write(vis, (APair.APairBoolean)b);
                else if( b instanceof APair.APairByte )write(vis, (APair.APairByte)b);
                else if( b instanceof APair.APairCharacter )write(vis, (APair.APairCharacter)b);
                else if( b instanceof APair.APairDouble )write(vis, (APair.APairDouble)b);
                else if( b instanceof APair.APairFloat )write(vis, (APair.APairFloat)b);
                else if( b instanceof APair.APairInteger )write(vis, (APair.APairInteger)b);
                else if( b instanceof APair.APairLong )write(vis, (APair.APairLong)b);
                else if( b instanceof APair.APairShort )write(vis, (APair.APairShort)b);
                else if( b instanceof APair.APairString )write(vis, (APair.APairString)b);
                else throw new UnsupportedOperationException("can't write "+b);
            }
        }
    }

    protected void write(AnnotationVisitor v, APair.APairBoolean a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairByte a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairCharacter a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairDouble a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairFloat a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairInteger a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairLong a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairShort a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, APair.APairString a){
        v.visit(a.getName(), a.getValue());
    }

    protected void write(AnnotationVisitor v, AEnd a){
        v.visitEnd();
    }

    protected void write(AnnotationVisitor v, AEnum a){
        v.visitEnum(a.getName(),a.getDescriptor(),a.getValue());
    }

    protected void write(AnnotationVisitor v, EmAArray ann){
        getNestedAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == ann.getEmbededAnnotationVisitorId()
        ).findFirst().ifPresent( nested -> {
            var nv = v.visitArray(ann.getName());
            write(nv,nested.getBody());
        });
    }

    protected void write(AnnotationVisitor v, EmANameDesc ann){
        getNestedAnnotations().stream().filter(
            f -> f.getDefinition()!=null && f.getDefinition().getAnnotationVisitorId() == ann.getEmbededAnnotationVisitorId()
        ).findFirst().ifPresent( nested -> {
            var nv = v.visitAnnotation(ann.getName(),ann.getDescriptor());
            write(nv,nested.getBody());
        });
    }

    protected void write(ClassWriter cw, FAnnotation ann){
    }

    protected void write(ClassWriter cw, FTypeAnnotation ann){
    }

    protected void write(ClassWriter cw, MAnnotation ann){
    }

    protected void write(ClassWriter cw, MAnnotationDefault ann){
    }
}
