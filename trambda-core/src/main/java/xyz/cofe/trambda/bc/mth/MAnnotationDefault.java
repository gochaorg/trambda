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
 * Visits the default value of this annotation interface method.
 *
 * <p>a visitor to the visit the actual default value of this annotation interface method, or
 * {@literal null} if this visitor is not interested in visiting this default value. The
 * 'name' parameters passed to the methods of this annotation visitor are ignored. Moreover,
 * exacly one visit method must be called on this annotation visitor, followed by visitEnd.
 */
public class MAnnotationDefault extends MAbstractBC
    implements ByteCode, AnnotationDef, GetAnnotationByteCodes, MethodWriter
{
    private static final long serialVersionUID = 1;

    public MAnnotationDefault(){}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MAnnotationDefault(MAnnotationDefault sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
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
    public MAnnotationDefault clone(){ return new MAnnotationDefault(this); }

    public String toString(){
        return MAnnotationDefault.class.getSimpleName();
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

        var av = v.visitAnnotationDefault();

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
