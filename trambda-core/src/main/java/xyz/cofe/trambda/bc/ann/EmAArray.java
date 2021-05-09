package xyz.cofe.trambda.bc.ann;

import java.util.ArrayList;
import org.objectweb.asm.AnnotationVisitor;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;

public class EmAArray extends EmbededAnnotation implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    public EmAArray(){
    }

    public EmAArray(EmAArray sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.getName();
        if( sample.annotationByteCodes!=null ){
            annotationByteCodes = new ArrayList<>();
            for( var b : sample.annotationByteCodes ){
                if( b!=null ){
                    annotationByteCodes.add(b.clone());
                }else{
                    annotationByteCodes.add(null);
                }
            }
        }
    }

    public EmAArray clone(){
        return new EmAArray(this);
    }

    //region name : String
    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion

    public String toString(){
        return EmAArray.class.getSimpleName()+" name="+name;
    }

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public Eterable<ByteCode> nodes(){
        if( annotationByteCodes!=null )return Eterable.of(annotationByteCodes);
        return Eterable.empty();
    }

    @Override
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var nv = v.visitArray(getName());
        var body = annotationByteCodes;
        if( body!=null ){
            for( var b : body ){
                if( b != null ){
                    ((AnnotationWriter)b).write(nv);
                }
            }
        }
    }
}
