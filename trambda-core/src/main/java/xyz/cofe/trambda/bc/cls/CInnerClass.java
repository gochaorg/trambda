package xyz.cofe.trambda.bc.cls;

import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.AccFlagsProperty;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.InnerClassFlags;

public class CInnerClass implements ClsByteCode, ClazzWriter, AccFlagsProperty, InnerClassFlags {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CInnerClass(){}
    public CInnerClass(String name, String outerName, String innerName, int access){
        this.name = name;
        this.outerName = outerName;
        this.innerName = innerName;
        this.access = access;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CInnerClass(CInnerClass sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.name;
        outerName = sample.outerName;
        innerName = sample.innerName;
        access = sample.access;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CInnerClass clone(){
        return new CInnerClass(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CInnerClass configure(Consumer<CInnerClass> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region name : String
    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region outerName : String
    protected String outerName;
    public String getOuterName(){
        return outerName;
    }
    public void setOuterName(String outerName){
        this.outerName = outerName;
    }
    //endregion
    //region innerName : String
    protected String innerName;
    public String getInnerName(){
        return innerName;
    }
    public void setInnerName(String innerName){
        this.innerName = innerName;
    }
    //endregion
    //region access : Int
    protected int access;
    public int getAccess(){
        return access;
    }
    public void setAccess(int access){
        this.access = access;
    }
    //endregion

    @Override
    public String toString(){
        return CInnerClass.class.getSimpleName() +
            " name=" + name +
            " outerName=" + outerName +
            " innerName=" + innerName +
            " access=" + access+("#"+new AccFlags(access).flags()) ;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitInnerClass(getName(),getOuterName(),getInnerName(),getAccess());
    }
}
