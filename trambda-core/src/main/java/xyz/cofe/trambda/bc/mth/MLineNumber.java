package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;

/**
 * Номер строки в исходном коде
 */
public class MLineNumber extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLineNumber(){}
    
    /**
     * Конструктор
     * @param line Номер строки в исходном коде
     * @param label С какой меткой связана
     */
    public MLineNumber(int line, String label){
        this.line = line;
        this.label = label;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MLineNumber(MLineNumber sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        line = sample.line;
        label = sample.label;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLineNumber clone(){ return new MLineNumber(this); }

    //region line : int - Номер строки в исходном коде
    private int line;
    /**
     * Возвращает номер строки в исходном коде
     * @return Номер строки в исходном коде
     */
    public int getLine(){
        return line;
    }
    
    /**
     * Указывает номер строки в исходном коде
     * @param line Номер строки в исходном коде
     */
    public void setLine(int line){
        this.line = line;
    }
    //endregion
    //region label : String - С какой меткой связана
    private String label;
    
    /**
     * Возвращает с какой меткой связана
     * @return С какой меткой связана
     */
    public String getLabel(){
        return label;
    }
    
    /**
     * Указывает с какой меткой связана
     * @param label С какой меткой связана
     */
    public void setLabel(String label){
        this.label = label;
    }
    //endregion

    public String toString(){
        return MLineNumber.class.getSimpleName()+
            " line="+line+
            " label="+label;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var l = getLabel();
        v.visitLineNumber(getLine(),
            l!=null ? ctx.labelCreateOrGet(l) : null
        );
    }
}
