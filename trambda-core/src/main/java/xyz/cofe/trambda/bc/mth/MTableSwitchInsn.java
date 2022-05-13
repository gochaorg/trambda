package xyz.cofe.trambda.bc.mth;

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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
 * 
 * <hr>
 * Tablewitch - это инструкция переменной длины. 
 * Сразу после кода операции tablewitch от нуля до трех байтов должны действовать как заполнители, 
 * так что defaultbyte1 начинается с адреса, кратного четырем байтам от начала текущего метода 
 * (кода операции его первой инструкции). 
 * Сразу после заполнения идут байты, составляющие три 32-битных значения со знаком: 
 * по умолчанию, младший и высокий. Сразу после этого следуют байты, составляющие серию 
 * 32-битных смещений со знаком старшего и младшего + 1 со знаком. 
 * Значение low должно быть меньше или равно high. 
 * 32-битные смещения со знаком + 1 (high-low + 1) обрабатываются как таблица переходов с отсчетом от 0. 
 * Каждое из этих 32-битных значений со знаком строится 
 * как (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | байт4.
 * 
 * <p> Индекс должен иметь тип int и извлекается из стека операндов. 
 * Если index меньше low или index больше high, то целевой адрес вычисляется 
 * путем добавления значения по умолчанию к адресу кода операции этой инструкции tablewitch. 
 * В противном случае извлекается смещение в позиции index - low таблицы переходов. 
 * Целевой адрес вычисляется путем добавления этого смещения к адресу кода операции этой инструкции tablewitch. 
 * Затем выполнение продолжается по целевому адресу.
 * 
 * <p> Целевой адрес, который может быть вычислен из каждого смещения таблицы переходов, 
 * а также адрес, который может быть вычислен по умолчанию, должен быть адресом кода 
 * операции инструкции внутри метода, который содержит эту инструкцию tablewitch.
 */
public class MTableSwitchInsn extends MAbstractBC implements ByteCode, MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MTableSwitchInsn(){}
    
    /**
     * Конструктор
     * @param min the minimum key value.
     * @param max the maximum key value.
     * @param dflt beginning of the default handler block.
     * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
     * handler block for the {@code min + i} key.
     */
    public MTableSwitchInsn(int min, int max, String dflt, String... labels){
        this.min = min;
        this.max = max;
        this.defaultLabel = dflt;
        this.labels = labels;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MTableSwitchInsn(MTableSwitchInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        min = sample.min;
        max = sample.max;
        defaultLabel = sample.defaultLabel;
        if( sample.labels!=null )labels = Arrays.copyOf(sample.labels, sample.labels.length);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MTableSwitchInsn clone(){ return new MTableSwitchInsn(this); }

    //region min : int
    private int min;

    /**
     * the minimum key value
     * @return  the minimum key value
     */
    public int getMin(){
        return min;
    }

    /**
     * the minimum key value
     * @param min the minimum key value
     */
    public void setMin(int min){
        this.min = min;
    }
    //endregion
    //region max : int
    private int max;

    /**
     * the maximum key value.
     * @return the maximum key value.
     */
    public int getMax(){
        return max;
    }

    /**
     * the maximum key value.
     * @param max the maximum key value.
     */
    public void setMax(int max){
        this.max = max;
    }
    //endregion
    //region defaultLabel : String
    private String defaultLabel;

    /**
     * beginning of the default handler block.
     * @return beginning of the default handler block.
     */
    public String getDefaultLabel(){
        return defaultLabel;
    }

    /**
     * beginning of the default handler block.
     * @param defaultLabel beginning of the default handler block.
     */
    public void setDefaultLabel(String defaultLabel){
        this.defaultLabel = defaultLabel;
    }
    //endregion
    //region labels : String[]
    private String[] labels;

    /**
     * beginnings of the handler blocks. {@code labels[i]} is the beginning of the
     * handler block for the {@code min + i} key.
     * @return handler blocks
     */
    public String[] getLabels(){
        return labels;
    }

    /**
     * beginnings of the handler blocks. {@code labels[i]} is the beginning of the
     * handler block for the {@code min + i} key.
     * @param labels handler blocks
     */
    public void setLabels(String[] labels){
        this.labels = labels;
    }
    //endregion

    public String toString(){
        return MTableSwitchInsn.class.getSimpleName()+
            " min="+min+
            " max="+max+
            (defaultLabel!=null ? " defLabel="+defaultLabel : "")+
            (labels!=null ? " label="+ List.of(labels) : "")
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var dl = getDefaultLabel();
        var ls = getLabels();

        v.visitTableSwitchInsn(
            getMin(), getMax(),
            dl!=null ? ctx.labelCreateOrGet(dl) : null,
            ctx.labelsGet(ls)
        );
    }
}
