package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.AccFlagsProperty;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ParameterFlags;

/**
 * Visits a parameter of this method.
 */
public class MParameter extends MAbstractBC implements MethodWriter, AccFlagsProperty, ParameterFlags {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MParameter(){}
    public MParameter(String name, int access){
        this.access = access;
        this.name = name;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MParameter(MParameter sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        access = sample.access;
        name = sample.name;
    }

    public MParameter clone(){ return new MParameter(this); }

    //region access : int
    private int access;

    /**
     * the parameter's access flags, only {@code ACC_FINAL}, {@code ACC_SYNTHETIC}
     * or/and {@code ACC_MANDATED} are allowed (see {@link Opcodes}).
     *
     * @return the parameter's access flags
     */
    public int getAccess(){
        return access;
    }

    /**
     * the parameter's access flags, only {@code ACC_FINAL}, {@code ACC_SYNTHETIC}
     * or/and {@code ACC_MANDATED} are allowed (see {@link Opcodes}).
     *
     * @param acc the parameter's access flags
     */
    public void setAccess(int acc){
        this.access = acc;
    }
    //endregion
    //region name : String
    private String name;

    /**
     * parameter name or {@literal null} if none is provided.
     * @return parameter name
     */
    public String getName(){
        return name;
    }

    /**
     * parameter name or {@literal null} if none is provided.
     * @param name parameter name
     */
    public void setName(String name){
        this.name = name;
    }
    //endregion

    public String toString(){
        return MParameter.class.getSimpleName()+
            " name="+name+
            " access="+access+"#"+new AccFlags(access).flags()
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitParameter(getName(),getAccess());
    }
}
