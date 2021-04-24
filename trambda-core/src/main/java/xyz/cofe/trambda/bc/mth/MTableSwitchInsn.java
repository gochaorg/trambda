package xyz.cofe.trambda.bc.mth;

import java.util.List;
import org.objectweb.asm.Label;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * tableswitch
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Access jump table by index and jump
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * tableswitch
 * &lt;0-3 byte pad&gt;
 * defaultbyte1
 * defaultbyte2
 * defaultbyte3
 * defaultbyte4
 * lowbyte1
 * lowbyte2
 * lowbyte3
 * lowbyte4
 * highbyte1
 * highbyte2
 * highbyte3
 * highbyte4
 * jump offsets...
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * tableswitch = 170 (0xaa)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., index →
 * ...
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * A tableswitch is a variable-length instruction. Immediately after the tableswitch opcode, between zero and three bytes must act as padding, such that defaultbyte1 begins at an address that is a multiple of four bytes from the start of the current method (the opcode of its first instruction). Immediately after the padding are bytes constituting three signed 32-bit values: default, low, and high. Immediately following are bytes constituting a series of high - low + 1 signed 32-bit offsets. The value low must be less than or equal to high. The high - low + 1 signed 32-bit offsets are treated as a 0-based jump table. Each of these signed 32-bit values is constructed as (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | byte4.
 *
 * <p>
 * The index must be of type int and is popped from the operand stack. If index is less than low or index is greater than high, then a target address is calculated by adding default to the address of the opcode of this tableswitch instruction. Otherwise, the offset at position index - low of the jump table is extracted. The target address is calculated by adding that offset to the address of the opcode of this tableswitch instruction. Execution then continues at the target address.
 *
 * <p>
 * The target address that can be calculated from each jump table offset, as well as the one that can be calculated from default, must be the address of an opcode of an instruction within the method that contains this tableswitch instruction.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * The alignment required of the 4-byte operands of the tableswitch instruction guarantees 4-byte alignment of those operands if and only if the method that contains the tableswitch starts on a 4-byte boundary.
 */
public class MTableSwitchInsn extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MTableSwitchInsn(){}
    public MTableSwitchInsn(int min, int max, String dflt, String... labels){
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
