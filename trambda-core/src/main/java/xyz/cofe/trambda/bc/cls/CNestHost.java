package xyz.cofe.trambda.bc.cls;

import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.ByteCode;

public class CNestHost implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CNestHost(){}
    public CNestHost(String nestHost){
        this.nestHost = nestHost;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CNestHost(CNestHost sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        nestHost = sample.nestHost;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CNestHost clone(){ return new CNestHost(this); }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CNestHost configure(Consumer<CNestHost> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region nestHost : String
    protected String nestHost;
    public String getNestHost(){ return nestHost; }
    public void setNestHost(String nestHost){
        this.nestHost = nestHost;
    }
    //endregion

    public String toString(){ return CNestHost.class.getSimpleName()+" nestHost="+nestHost; }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitNestHost(getNestHost());
    }
}
