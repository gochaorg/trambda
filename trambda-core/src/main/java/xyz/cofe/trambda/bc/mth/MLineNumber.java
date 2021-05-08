package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

public class MLineNumber extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MLineNumber(){}
    public MLineNumber(int line, String label){
        this.line = line;
        this.label = label;
    }

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
}
