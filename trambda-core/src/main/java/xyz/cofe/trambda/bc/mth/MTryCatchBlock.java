package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * try catch block
 */
public class MTryCatchBlock extends MAbstractBC
    implements ByteCode, MethodWriter
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MTryCatchBlock(){}

    public MTryCatchBlock(String start, String end, String handler, String type){
        this.labelStart = start;
        this.labelEnd = end;
        this.labelHandler = handler;
        this.type = type;
        System.out.println("MTryCatchBlock("+start+","+end+","+handler+","+type+")");
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MTryCatchBlock(MTryCatchBlock sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        labelStart = sample.labelStart;
        labelEnd = sample.labelEnd;
        labelHandler = sample.labelHandler;
        type = sample.type;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MTryCatchBlock clone(){ return new MTryCatchBlock(this); }

    //region labelStart : String
    private String labelStart;

    /**
     * the beginning of the exception handler's scope (inclusive).
     * @return begin label
     */
    public String getLabelStart(){
        return labelStart;
    }

    public void setLabelStart(String labelStart){
        this.labelStart = labelStart;
    }
    //endregion
    //region labelEnd : String
    private String labelEnd;

    /**
     * the end of the exception handler's scope (exclusive).
     * @return end label
     */
    public String getLabelEnd(){
        return labelEnd;
    }
    public void setLabelEnd(String labelEnd){
        this.labelEnd = labelEnd;
    }
    //endregion
    //region labelHandler : String
    private String labelHandler;

    /**
     * the beginning of the exception handler's code.
     * @return handler
     */
    public String getLabelHandler(){
        return labelHandler;
    }

    public void setLabelHandler(String labelHandler){
        this.labelHandler = labelHandler;
    }
    //endregion
    //region type : String
    private String type;

    /**
     * the internal name of the type of exceptions handled by the handler, or {@literal null}
     * to catch any exceptions (for "finally" blocks).
     * @return type
     */
    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
    //endregion

    public String toString(){
        return MTryCatchBlock.class.getSimpleName()+
            " start="+labelStart+
            " end="+labelEnd+
            " handler="+labelHandler+
            " type="+type;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var ls = getLabelStart();
        var le = getLabelEnd();
        var lh = getLabelHandler();
        v.visitTryCatchBlock(
            ls!=null ? ctx.labelCreateOrGet(ls) : null,
            le!=null ? ctx.labelCreateOrGet(le) : null,
            lh!=null ? ctx.labelCreateOrGet(lh) : null,
            getType()
        );
    }
}
