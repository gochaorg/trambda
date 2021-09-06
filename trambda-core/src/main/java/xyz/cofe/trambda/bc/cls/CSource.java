package xyz.cofe.trambda.bc.cls;

import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Содержит имя исходного класса/файла отладки (debug)
 */
public class CSource implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    public CSource(){
    }
    public CSource(String source, String debug){
        this.source = source;
        this.debug = debug;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CSource(CSource sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        source = sample.source;
        debug = sample.debug;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CSource clone(){
        return new CSource(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CSource configure(Consumer<CSource> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region source : String
    protected String source;
    public String getSource(){
        return source;
    }
    public void setSource(String source){
        this.source = source;
    }
    //endregion
    //region debug : String
    protected String debug;

    public String getDebug(){
        return debug;
    }

    public void setDebug(String debug){
        this.debug = debug;
    }
    //endregion

    @Override
    public String toString(){
        return CSource.class.getSimpleName()+" " +
            "source=" + (source!=null ? "\""+source+"\"" : "null") +
            ", debug=" + (debug!=null ? "\""+debug+"\"" : "null");
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitSource(getSource(),getDebug());
    }
}
