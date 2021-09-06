package xyz.cofe.trambda.bc.ann;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;

public class EmANameDesc extends EmbededAnnotation implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public EmANameDesc(){
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public EmANameDesc(EmANameDesc sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        name = sample.getName();
        descriptor = sample.getDescriptor();

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

    public EmANameDesc clone(){
        return new EmANameDesc(this);
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
    //region descriptor : String
    protected String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion

    public String toString(){
        return EmANameDesc.class.getSimpleName()+" name="+name+" descriptor="+descriptor;
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
        var nv = v.visitAnnotation(getName(),getDescriptor());
        var body = annotationByteCodes;
        if( body!=null ){
            for( var b : body ){
                if( b instanceof AnnotationWriter ){
                    ((AnnotationWriter)b).write(nv);
                }
            }
        }
    }
}
