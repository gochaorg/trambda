package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;

/**
 * максимальный размер стека и максимальное количество локальных переменных метода.
 */
public class MMaxs extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MMaxs(){
    }

    /**
     * Конструктор
     * @param maxStack максимальный размер стека метода.
     * @param maxLocals максимальное количество локальных переменных для метода.
     */
    public MMaxs(int maxStack, int maxLocals){
        this.maxLocals = maxLocals;
        this.maxStack = maxStack;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MMaxs(MMaxs sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        maxLocals = sample.maxLocals;
        maxStack = sample.maxStack;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MMaxs clone(){ return new MMaxs(this); }

    //region maxStack : int
    private int maxStack;
    
    /**
     * Возвращает максимальный размер стека метода.
     * @return максимальный размер стека метода.
     */
    public int getMaxStack(){
        return maxStack;
    }
    
    /**
     * Указывает максимальный размер стека метода.
     * @param maxStack максимальный размер стека метода.
     */
    public void setMaxStack(int maxStack){
        this.maxStack = maxStack;
    }
    //endregion
    //region maxLocals : int
    private int maxLocals;
    
    /**
     * Возвращает максимальное количество локальных переменных для метода.
     * @return максимальное количество локальных переменных для метода.
     */
    public int getMaxLocals(){
        return maxLocals;
    }
    
    /**
     * Указывает максимальное количество локальных переменных для метода.
     * @param maxLocals максимальное количество локальных переменных для метода.
     */
    public void setMaxLocals(int maxLocals){
        this.maxLocals = maxLocals;
    }
    //endregion

    public String toString(){
        return MMaxs.class.getSimpleName()+
            " stack="+maxStack+
            " locals="+maxLocals+"";
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitMaxs(getMaxStack(),getMaxLocals());
    }
}
