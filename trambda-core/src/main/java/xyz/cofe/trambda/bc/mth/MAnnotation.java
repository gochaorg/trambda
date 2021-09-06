package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

/**
 * Visits an annotation of this method.
 *
 * <p>
 * {@link #descriptor} the class descriptor of the annotation class.
 *
 * <p>
 * {@link #visible}  {@literal true} if the annotation is visible at runtime.
 *
 * <p>
 * a visitor to visit the annotation values, or {@literal null} if this visitor is not
 * interested in visiting this annotation.
 */
public class MAnnotation
    extends MAbstractBC
    implements ByteCode, AnnotationDef, GetAnnotationByteCodes, MethodWriter
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MAnnotation(){}
    public MAnnotation(String descriptor, boolean visible){
        this.descriptor = descriptor;
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MAnnotation(MAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.descriptor = sample.getDescriptor();
        this.visible = sample.isVisible();
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

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MAnnotation clone(){ return new MAnnotation(this); }

    //region descriptor : String
    protected String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region visible : boolean
    protected boolean visible;
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
    }
    //endregion

    public String toString(){
        return MAnnotation.class.getSimpleName()+
            " descriptor="+descriptor+
            " visible="+visible;
    }

    //region annotationByteCodes : List<AnnotationByteCode>
    protected List<AnnotationByteCode> annotationByteCodes;
    public List<AnnotationByteCode> getAnnotationByteCodes(){
        if(annotationByteCodes==null)annotationByteCodes = new ArrayList<>();
        return annotationByteCodes;
    }
    public void setAnnotationByteCodes(List<AnnotationByteCode> byteCodes){
        annotationByteCodes = byteCodes;
    }
    //endregion

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
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        var av = v.visitAnnotation(getDescriptor(),isVisible());

        var abody = annotationByteCodes;
        if( abody!=null ){
            var i = -1;
            for( var ab : abody ){
                i++;
                if( ab!=null ){
                    ab.write(av);
                }else{
                    throw new IllegalStateException("annotationByteCodes["+i+"]==null");
                }
            }
        }
    }
}
