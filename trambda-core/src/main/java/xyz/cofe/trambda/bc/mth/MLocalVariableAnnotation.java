package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnVisIdProperty;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

public class MLocalVariableAnnotation extends MAbstractBC
    implements
    ByteCode, AnnVisIdProperty, AnnotationDef, GetAnnotationByteCodes, MethodWriter
{
    private static final long serialVersionUID = 1;

    public MLocalVariableAnnotation(){}

    //region annotationVisitorId : int
    protected int annotationVisitorId = AnnVisIdProperty.DEF_ANNOTATION_VISITOR_ID;
    public int getAnnotationVisitorId(){ return annotationVisitorId; };
    public void setAnnotationVisitorId(int id){
        this.annotationVisitorId = id;
    }
    //endregion
    //region typeRef : int
    protected int typeRef;
    public int getTypeRef(){
        return typeRef;
    }

    public void setTypeRef(int typeRef){
        this.typeRef = typeRef;
    }
    //endregion
    //region typePath : String
    protected String typePath;
    public String getTypePath(){
        return typePath;
    }

    public void setTypePath(String typePath){
        this.typePath = typePath;
    }
    //endregion
    //region startLabels : String[]
    protected String[] startLabels;
    public String[] getStartLabels(){
        return startLabels;
    }

    public void setStartLabels(String[] startLabels){
        this.startLabels = startLabels;
    }
    //endregion
    //region endLabels : String[]
    protected String[] endLabels;
    public String[] getEndLabels(){
        return endLabels;
    }

    public void setEndLabels(String[] endLabels){
        this.endLabels = endLabels;
    }
    //endregion
    //region index : int[]
    protected int[] index;
    public int[] getIndex(){
        return index;
    }

    public void setIndex(int[] index){
        this.index = index;
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
        return MLocalVariableAnnotation.class.getSimpleName()+
            " typeRef="+typeRef+
            " typePath="+typePath+
            " startLabels="+ Arrays.toString(startLabels) +
            " endLabels="+ Arrays.toString(endLabels) +
            " index="+ Arrays.toString(index) +
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
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var tp = getTypePath();

        var av = v.visitLocalVariableAnnotation(
            getTypeRef(),
            tp!=null ? TypePath.fromString(tp) : null,
            ctx.labelsGet(getStartLabels()),
            ctx.labelsGet(getEndLabels()),
            getIndex(),
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
