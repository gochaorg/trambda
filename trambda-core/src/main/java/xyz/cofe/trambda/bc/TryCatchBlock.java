package xyz.cofe.trambda.bc;

public class TryCatchBlock implements ByteCode {
    private static final long serialVersionUID = 1;

    public TryCatchBlock(){}

    public TryCatchBlock(String start, String end, String handler, String type){
        this.labelStart = start;
        this.labelEnd = end;
        this.labelHandler = handler;
        this.type = type;
    }

    //region start
    private String labelStart;
    public String getLabelStart(){
        return labelStart;
    }

    public void setLabelStart(String labelStart){
        this.labelStart = labelStart;
    }
    //endregion
    //region end
    private String labelEnd;
    public String getLabelEnd(){
        return labelEnd;
    }
    public void setLabelEnd(String labelEnd){
        this.labelEnd = labelEnd;
    }
    //endregion
    //region handler
    private String labelHandler;

    public String getLabelHandler(){
        return labelHandler;
    }

    public void setLabelHandler(String labelHandler){
        this.labelHandler = labelHandler;
    }
    //endregion
    //region type
    private String type;

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
    //endregion

    public String toString(){
        return "TryCatchBlock start="+labelStart+" end="+labelEnd+" handler="+labelHandler+" type="+type;
    }
}
