package xyz.cofe.trambda.bc.ann;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;

public class EmANameDesc extends EmbededAnnotation implements AnnotationWriter {
    private static final long serialVersionUID = 1;

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
