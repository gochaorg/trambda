package xyz.cofe.trambda.bc.mth;

import java.util.Arrays;
import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * lookupswitch
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Access jump table by key match and jump
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * lookupswitch
 * &lt;0-3 byte pad&gt;
 * defaultbyte1
 * defaultbyte2
 * defaultbyte3
 * defaultbyte4
 * npairs1
 * npairs2
 * npairs3
 * npairs4
 * match-offset pairs...
 * </pre>
 * 
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * lookupswitch = 171 (0xab)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * ..., key →
 *
 * ...
 * <h2 style="font-weight: bold">Description</h2>
 *
 * A lookupswitch is a variable-length instruction. Immediately after the lookupswitch opcode, between zero and three bytes must act as padding, such that defaultbyte1 begins at an address that is a multiple of four bytes from the start of the current method (the opcode of its first instruction). Immediately after the padding follow a series of signed 32-bit values: default, npairs, and then npairs pairs of signed 32-bit values. The npairs must be greater than or equal to 0. Each of the npairs pairs consists of an int match and a signed 32-bit offset. Each of these signed 32-bit values is constructed from four unsigned bytes as (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | byte4.
 *
 * <p>
 * The table match-offset pairs of the lookupswitch instruction must be sorted in increasing numerical order by match.
 *
 * <p>
 * The key must be of type int and is popped from the operand stack. The key is compared against the match values. If it is equal to one of them, then a target address is calculated by adding the corresponding offset to the address of the opcode of this lookupswitch instruction. If the key does not match any of the match values, the target address is calculated by adding default to the address of the opcode of this lookupswitch instruction. Execution then continues at the target address.
 *
 * <p>
 * The target address that can be calculated from the offset of each match-offset pair, as well as the one calculated from default, must be the address of an opcode of an instruction within the method that contains this lookupswitch instruction.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * The alignment required of the 4-byte operands of the lookupswitch instruction guarantees 4-byte alignment of those operands if and only if the method that contains the lookupswitch is positioned on a 4-byte boundary.
 *
 * <p>
 * The match-offset pairs are sorted to support lookup routines that are quicker than linear search.
 */
public class MLookupSwitchInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLookupSwitchInsn(){}
    public MLookupSwitchInsn(String defHdl, int[] keys, String[] labels){
        this.defaultHandlerLabel = defHdl;
        this.keys = keys;
        this.labels = labels;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MLookupSwitchInsn(MLookupSwitchInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        defaultHandlerLabel = sample.defaultHandlerLabel;
        if( sample.keys!=null )keys = Arrays.copyOf(sample.keys,sample.keys.length);
        if( sample.labels!=null )labels = Arrays.copyOf(sample.labels,sample.labels.length);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLookupSwitchInsn clone(){ return new MLookupSwitchInsn(this); }

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
        return MLookupSwitchInsn.class.getSimpleName()+" defHandlerLabel="+defaultHandlerLabel+(
            keys!=null ? " keys="+Arrays.asList(keys) : ""
            )+(
                labels!=null ? " label="+Arrays.asList(labels) : ""
            );
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var dl = getDefaultHandlerLabel();
        v.visitLookupSwitchInsn(
            dl!=null ? ctx.labelGet(dl) : null,
            getKeys(),
            ctx.labelsGet(getLabels())
        );
    }
}
