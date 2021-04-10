package xyz.cofe.trambda.bc;

import java.util.Arrays;

public class LookupSwitchInsn implements ByteCode {
    private static final long serialVersionUID = 1;
    public LookupSwitchInsn(){}
    public LookupSwitchInsn(String defHdl, int[] keys, String[] labels){
        this.defaultHandlerLabel = defHdl;
        this.keys = keys;
        this.labels = labels;
    }
    //region defaultHandlerLabel
    private String defaultHandlerLabel;

    public String getDefaultHandlerLabel(){
        return defaultHandlerLabel;
    }
    public void setDefaultHandlerLabel(String defaultHandlerLabel){
        this.defaultHandlerLabel = defaultHandlerLabel;
    }
    //endregion
    //region keys : int[]
    private int[] keys;
    public int[] getKeys(){
        return keys;
    }

    public void setKeys(int[] keys){
        this.keys = keys;
    }
    //endregion
    //region labels : String[]
    private String[] labels;

    public String[] getLabels(){
        return labels;
    }

    public void setLabels(String[] labels){
        this.labels = labels;
    }
    //endregion

    public String toString(){
        return "LookupSwitchInsn defHandlerLabel="+defaultHandlerLabel+(
            keys!=null ? " keys="+Arrays.asList(keys) : ""
            )+(
                labels!=null ? " label="+Arrays.asList(labels) : ""
            );
    }
}
