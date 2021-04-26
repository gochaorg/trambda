package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;

public class Annotation {
    protected final List<? extends ByteCode> byteCode;
    public Annotation(AnnotationDef<? extends ByteCode> definition, List<? extends ByteCode> byteCode, boolean copy ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( definition==null )throw new IllegalArgumentException( "definition==null" );
        this.byteCode = Collections.unmodifiableList(copy ? new ArrayList<>(byteCode) : byteCode);
        this.definition = definition;
        body = byteCode.stream()
            .map( a -> a instanceof AnnotationByteCode ? (AnnotationByteCode)a : null )
            .filter( Objects::nonNull )
            .filter( a -> a.getAnnotationVisitorId() == definition.getAnnotationDefVisitorId() )
            .collect(Collectors.toUnmodifiableList());
    }

    //region definition : AnnotationDef<? extends ByteCode>
    protected final AnnotationDef<? extends ByteCode> definition;
    public AnnotationDef<? extends ByteCode> getDefinition(){
        return definition;
    }
    //endregion
    //region body : List<AnnotationByteCode>
    protected final List<AnnotationByteCode> body;
    public List<AnnotationByteCode> getBody(){ return body; }
    //endregion
    //region nestedAnnotation : List<Annotation>
    protected List<Annotation> nestedAnnotations;
    public List<Annotation> getNestedAnnotations(){
        if( nestedAnnotations!=null )return nestedAnnotations;
        nestedAnnotations = body.stream().map( a -> a instanceof AnnotationDef ? (AnnotationDef<?>)a : null )
            .filter(Objects::nonNull)
            .map( a -> new Annotation(a,byteCode,false) )
            .collect(Collectors.toUnmodifiableList());
        return nestedAnnotations;
    }
    //endregion

    public void visit(BiConsumer<Annotation,List<Annotation>> path){
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
}
