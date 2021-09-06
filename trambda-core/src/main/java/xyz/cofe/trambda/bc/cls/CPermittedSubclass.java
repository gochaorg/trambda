package xyz.cofe.trambda.bc.cls;

import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.ByteCode;

public class CPermittedSubclass implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CPermittedSubclass(){}
    public CPermittedSubclass(String permittedSubclass){
        this.permittedSubclass = permittedSubclass;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CPermittedSubclass(CPermittedSubclass sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        permittedSubclass = sample.permittedSubclass;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CPermittedSubclass clone(){
        return new CPermittedSubclass(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CPermittedSubclass configure(Consumer<CPermittedSubclass> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region permittedSubclass : String
    protected String permittedSubclass;

    public String getPermittedSubclass(){
        return permittedSubclass;
    }

    public void setPermittedSubclass(String permittedSubclass){
        this.permittedSubclass = permittedSubclass;
    }
    //endregion

    @Override
    public String toString(){
        return CPermittedSubclass.class.getSimpleName()+" " +
            "permittedSubclass=" + permittedSubclass;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitPermittedSubclass(getPermittedSubclass());
    }
}
