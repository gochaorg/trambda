package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

public class CAnnotation implements
    ClsByteCode, AnnotationDef, GetAnnotationByteCodes,
    ClazzWriter
{
    private static final long serialVersionUID = 1;

    public CAnnotation(){}
    public CAnnotation(String descriptor, boolean visible){
        this.descriptor = descriptor;
        this.visible = visible;
    }

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

    //region toString()
    public String toString(){
        return CAnnotation.class.getSimpleName()+
            " descriptor="+descriptor+
            " visible="+visible;
    }
    //endregion

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

        var av = v.visitAnnotation(getDescriptor(), isVisible());

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
