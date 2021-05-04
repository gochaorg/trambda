package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

/**
 * try catch block
 */
public class MTryCatchBlock extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MTryCatchBlock(){}

    public MTryCatchBlock(String start, String end, String handler, String type){
        this.labelStart = start;
        this.labelEnd = end;
        this.labelHandler = handler;
        this.type = type;
    }

    //region start
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
    //region end
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
    //region handler
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
    //region type
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
        return MTryCatchBlock.class.getSimpleName()+" start="+labelStart+" end="+labelEnd+" handler="+labelHandler+" type="+type;
    }
}
