package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

public class MLineNumber extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLineNumber(){}
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

    //region line : int
    private int line;
    public int getLine(){
        return line;
    }
    public void setLine(int line){
        this.line = line;
    }
    //endregion
    //region label : String
    private String label;
    public String getLabel(){
        return label;
    }
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
