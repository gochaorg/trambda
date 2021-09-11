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
 * 
 * <hr>
 * Поисковый переключатель - это инструкция переменной длины. Сразу после кода операции lookupswitch от нуля до трех байтов должны действовать как заполнители, так что defaultbyte1 начинается с адреса, кратного четырем байтам от начала текущего метода (кода операции его первой инструкции). Сразу после заполнения следует ряд 32-битных значений со знаком: default, npairs, а затем npairs пары 32-битных значений со знаком. Число npairs должно быть больше или равно 0. Каждая из пар npairs состоит из соответствия int и 32-битного смещения со знаком. Каждое из этих 32-битных значений со знаком состоит из четырех байтов без знака как (byte1 & lt; & lt; 24) | (byte2 & lt; & lt; 16) | (byte3 & lt; & lt; 8) | байт4.
 * 
 * <p> Пары совпадения-смещения таблицы инструкции lookupswitch должны быть отсортированы в возрастающем числовом порядке по совпадению.
 * <p> Ключ должен иметь тип int и извлекается из стека операндов. Ключ сравнивается со значениями соответствия. Если он равен одному из них, то целевой адрес вычисляется путем добавления соответствующего смещения к адресу кода операции этой инструкции lookupswitch. Если ключ не соответствует ни одному из значений соответствия, целевой адрес вычисляется путем добавления значения по умолчанию к адресу кода операции этой инструкции lookupswitch. Затем выполнение продолжается по целевому адресу.
 * <p> Целевой адрес, который может быть вычислен по смещению каждой пары совпадение-смещение, а также адрес, вычисленный по умолчанию, должен быть адресом кода операции инструкции в методе, который содержит эту инструкцию переключателя поиска.
 * 
 * <h2 style = "font-weight: bold"> Примечания </h2>
 * Выравнивание, необходимое для 4-байтовых операндов инструкции lookupwitch, гарантирует 4-байтовое выравнивание этих операндов тогда и только тогда, когда метод, который содержит lookupswitch, расположен на 4-байтовой границе.
 * <p> Пары совпадения-смещения сортируются для поддержки процедур поиска, которые работают быстрее, чем линейный поиск.
 */
public class MLookupSwitchInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLookupSwitchInsn(){}
    
    /**
     * Конструктор
     * @param defHdl начало блока обработчика по умолчанию.
     * @param keys значения ключей
     * @param labels начала блоков обработчика. {@code labels [i]} - начало
     * блок-обработчик для ключа {@code keys [i]}.
     */
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

    //region defaultHandlerLabel - начало блока обработчика по умолчанию.
    private String defaultHandlerLabel;

    /**
     * Возвращает начало блока обработчика по умолчанию.
     * @return начало блока обработчика по умолчанию.
     */
    public String getDefaultHandlerLabel(){
        return defaultHandlerLabel;
    }
    
    /**
     * Указывает начало блока обработчика по умолчанию.
     * @param defaultHandlerLabel начало блока обработчика по умолчанию.
     */
    public void setDefaultHandlerLabel(String defaultHandlerLabel){
        this.defaultHandlerLabel = defaultHandlerLabel;
    }
    //endregion
    //region keys : int[]
    private int[] keys;
    
    /**
     * Возвращает значения ключей
     * @return значения ключей
     */
    public int[] getKeys(){
        return keys;
    }
    
    /**
     * Указывает значения ключей
     * @param keys значения ключей
     */
    public void setKeys(int[] keys){
        this.keys = keys;
    }
    //endregion
    //region labels : String[]
    private String[] labels;

    /**
     * Возвращает начала блоков обработчика. {@code labels [i]} - начало
     * блок-обработчик для ключа {@code keys [i]}.
     * @return начала соответ блока
     */
    public String[] getLabels(){
        return labels;
    }

    /**
     * Указывает  начала блоков обработчика. {@code labels [i]} - начало
     * блок-обработчик для ключа {@code keys [i]}.
     * @param labels начала соответ блока
     */
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
