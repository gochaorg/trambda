package xyz.cofe.trambda.bc.cls;

import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.ByteCode;

public class CNestMember implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CNestMember(){}
    public CNestMember(String nestMember){
        this.nestMember = nestMember;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CNestMember(CNestMember sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        nestMember = sample.nestMember;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CNestMember clone(){ return new CNestMember(this); }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CNestMember configure(Consumer<CNestMember> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region nestMember : String
    protected String nestMember;
    public String getNestMember(){
        return nestMember;
    }
    public void setNestMember(String nestMember){
        this.nestMember = nestMember;
    }
    //endregion

    @Override
    public String toString(){
        return CNestMember.class.getSimpleName()+" " +
            "nestMember='" + nestMember + '\'' ;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitNestMember(getNestMember());
    }
}
