package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

/**
 * Аннотация прикрепленная к классу
 * <p>
 *
Есть такие классы
<pre>
public @interface Desc {
  String value();
}
 
<b>&#64;Desc("sample User2")</b>
public class User2 {
    public User2(){}
    public User2(String name1){
        this.name = name1;
    }

    &#64;Desc("name of user")
    private String name;

    &#64;Desc("name of user")
    public String getName(){ return name; }
    public void setName( @Required @MaxLength(100) @MinLength(1) String name ){ this.name = name; }

    private List<String> emails;
    &#64;Desc("emails of user")
    public List<String> getEmails(){ return emails; }
    public void setEmails( List<String> emails ){ this.emails = emails; }
}
</pre>

 В результате будет
 
 <pre>
 &lt;CBegin
    version="55"
    access="33"
    name="xyz/cofe/bc/xml/clss/User2"
    superName="java/lang/Object" interface="false"&gt;

    &lt;CSource order="0" source="User2.java"/&gt;
     &lt;CAnnotation
           order="1"
           visible="true"
           descriptor="Lxyz/cofe/bc/xml/clss/Desc;"&gt;

         &lt;APairString name="value"&gt;
             &lt;APairStringValue&gt;sample User2&lt;/APairStringValue&gt;
         &lt;/APairString&gt;
         &lt;AEnd/&gt;
     &lt;/CAnnotation&gt;
 </pre>
 */
public class CAnnotation implements
    ClsByteCode, AnnotationDef, GetAnnotationByteCodes,
    ClazzWriter
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CAnnotation(){}

    /**
     * Конструктор
     * @param descriptor имя типа аннотации
     * @param visible {@literal true} если аннотация видна в runtime
     */
    public CAnnotation(String descriptor, boolean visible){
        this.descriptor = descriptor;
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CAnnotation(CAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
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
    public CAnnotation clone(){
        return new CAnnotation(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CAnnotation configure(Consumer<CAnnotation> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region descriptor : String - Имя типа аннотации (дескриптор)
    /** Имя типа аннотации */
    protected String descriptor;

    /**
     * Возвращает имя типа аннотации
     * @return имя типа аннотации
     */
    public String getDescriptor(){
        return descriptor;
    }

    /**
     * Указывает имя типа аннотации
     * @param descriptor имя типа аннотации
     */
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region visible : boolean - видна ли аннотация в runtime
    /**
     * true если аннотация видна в runtime
     */
    protected boolean visible;

    /**
     * Возвращает видна ли аннотация в runtime
     * @return true если аннотация видна в runtime
     */
    public boolean isVisible(){
        return visible;
    }

    /**
     * Указывает видна ли аннотация в runtime
     * @param visible true если аннотация видна в runtime
     */
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

    //region annotationByteCodes : List<AnnotationByteCode> - вложенные параметры аннотации
    /**
     * вложенные параметры аннотации
     */
    protected List<AnnotationByteCode> annotationByteCodes;

    /**
     * Указывает вложенные параметры аннотации
     * @return вложенные параметры аннотации
     */
    public List<AnnotationByteCode> getAnnotationByteCodes(){
        if(annotationByteCodes==null)annotationByteCodes = new ArrayList<>();
        return annotationByteCodes;
    }

    /**
     * Возвращает вложенные параметры аннотации
     * @param byteCodes вложенные параметры аннотации
     */
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
