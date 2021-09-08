package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

public class CTypeAnnotation
    implements ClsByteCode, AnnotationDef, GetAnnotationByteCodes,
    ClazzWriter
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CTypeAnnotation(){
    }

    public CTypeAnnotation(int typeRef, String typePath, String descriptor, boolean visible){
        this.typeRef = typeRef;
        this.typePath = typePath;
        this.descriptor = descriptor;
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CTypeAnnotation(CTypeAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        typePath = sample.typePath;
        typeRef = sample.typeRef;
        descriptor = sample.descriptor;
        visible = sample.visible;

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
    public CTypeAnnotation clone(){
        return new CTypeAnnotation(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CTypeAnnotation configure(Consumer<CTypeAnnotation> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region typeRef : int
    /**
     *  a reference to the annotated type. The sort of this type reference must be
     * {@link TypeReference#CLASS_TYPE_PARAMETER},
     * {@link TypeReference#CLASS_TYPE_PARAMETER_BOUND} or
     * {@link TypeReference#CLASS_EXTENDS}
     * See {@link TypeReference}.
     */
    protected int typeRef;
    public int getTypeRef(){
        return typeRef;
    }
    public void setTypeRef(int typeRef){
        this.typeRef = typeRef;
    }
    //endregion
    //region typePath : String
    /**
     * the path to the annotated type argument, wildcard bound, array element type, or
     * static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     * 'typeRef' as a whole.
     */
    protected String typePath;
    public String getTypePath(){
        return typePath;
    }
    public void setTypePath(String typePath){
        this.typePath = typePath;
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
        return CTypeAnnotation.class.getSimpleName()+
            " typeRef="+typeRef+" typePath="+typePath+" descriptor="+descriptor+" visible="+visible;
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
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        var tp = getTypePath();
        var av = v.visitTypeAnnotation(
            getTypeRef(),
            tp!=null ? TypePath.fromString(tp) : null,
            getDescriptor(),
            isVisible()
        );

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
