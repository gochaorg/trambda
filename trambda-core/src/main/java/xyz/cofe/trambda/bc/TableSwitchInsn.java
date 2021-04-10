package xyz.cofe.trambda.bc;

import java.util.List;
import org.objectweb.asm.Label;

public class TableSwitchInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public TableSwitchInsn(){}
    public TableSwitchInsn(int min, int max, String dflt, String... labels){
        this.min = min;
        this.max = max;
        this.defaultLabel = dflt;
        this.labels = labels;
    }

    //region min
    private int min;

    public int getMin(){
        return min;
    }

    public void setMin(int min){
        this.min = min;
    }
    //endregion
    //region max
    private int max;

    public int getMax(){
        return max;
    }

    public void setMax(int max){
        this.max = max;
    }
    //endregion
    //region defaultLabel
    private String defaultLabel;

    public String getDefaultLabel(){
        return defaultLabel;
    }

    public void setDefaultLabel(String defaultLabel){
        this.defaultLabel = defaultLabel;
    }
    //endregion
    //region labels
    private String[] labels;

    public String[] getLabels(){
        return labels;
    }

    public void setLabels(String[] labels){
        this.labels = labels;
    }
    //endregion

    public String toString(){
        return "TableSwitchInsn"+
            " min="+min+
            " max="+max+
            (defaultLabel!=null ? " defLabel="+defaultLabel : "")+
            (labels!=null ? " label="+ List.of(labels) : "")
            ;
    }
}
