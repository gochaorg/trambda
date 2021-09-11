package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Операции {@link OpCode#BIPUSH}, {@link OpCode#SIPUSH}, {@link OpCode#NEWARRAY}
 * 
 * <h2>BIPUSH</h2>
 * Непосредственный байт расширяется знаком до значения типа int. Это значение помещается в стек операндов.
 * 
 * <h2>SIPUSH</h2>
 * Непосредственные значения без знака byte1 и byte2 собираются в промежуточное короткое замыкание, 
 * где значение короткого замыкания равно (byte1 &lt;&lt; 8) | байт2. 
 * Затем промежуточное значение расширяется знаком до значения типа int. 
 * Это значение помещается в стек операндов.
 * 
 * <h2>SIPUSH</h2>
 * A new array whose components are of type atype and of length count is allocated from 
 * the garbage-collected heap. 
 * A reference arrayref to this new array object is pushed into the operand stack. 
 * Each of the elements of the new array is initialized to the default initial value (§2.3, §2.4) 
 * for the element type of the array type.
 * 
 * <p>
 * Новый массив, компоненты которого имеют тип atype и счетчик длины, 
 * выделяется из кучи со сборкой мусора. Ссылка arrayref на этот новый объект 
 * массива помещается в стек операндов. Каждый из элементов нового массива 
 * инициализируется начальным значением по умолчанию (§2.3, §2.4) для типа элемента типа массива.
 * 
 * <pre>
 * ..., count → ..., arrayref
 * </pre>
 * 
 * Run-time Exception
 * <p>If count is less than zero, newarray throws a NegativeArraySizeException.
 * 
 * <p>Notes
 * <p>
 * In Oracle's Java Virtual Machine implementation, arrays of type boolean (atype is T_BOOLEAN) 
 * are stored as arrays of 8-bit values and are manipulated using the baload and bastore instructions 
 * (§baload, §bastore) which also access arrays of type byte. 
 * Other implementations may implement packed boolean arrays; the baload and bastore 
 * instructions must still be used to access those arrays.
 * 
 * <p>
 * В реализации виртуальной машины Java Oracle массивы типа boolean (тип - T_BOOLEAN) 
 * хранятся как массивы 8-битных значений и управляются с помощью инструкций baload и bastore 
 * (§baload, §bastore), которые также обращаются к массивам типа byte. Другие реализации могут 
 * реализовывать упакованные логические массивы; инструкции baload и bastore по-прежнему 
 * должны использоваться для доступа к этим массивам.
 * 
 * <p>
 * Visits an instruction with a single int operand.
 *
 * <br>{@link #opcode}  the opcode of the instruction to be visited. This opcode is either BIPUSH, SIPUSH
 *                or NEWARRAY.
 * <br>{@link #operand} the operand of the instruction to be visited.<br>
 *                When opcode is BIPUSH, operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.
 *                <br>
 *                When opcode is SIPUSH, operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.
 *                <br>
 *                When opcode is NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link
 *                Opcodes#T_CHAR}, {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE},
 *                {@link Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
 */
public class MIntInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MIntInsn(){}
    public MIntInsn(int op, int operand){
        this.opcode = op;
        this.operand = operand;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MIntInsn(MIntInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        opcode = sample.getOpcode();
        operand = sample.getOperand();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MIntInsn clone(){ return new MIntInsn(this); }

    //region opcode : int
    private int opcode;

    public int getOpcode(){
        return opcode;
    }

    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region operand : int
    private int operand;

    public int getOperand(){
        return operand;
    }

    public void setOperand(int operand){
        this.operand = operand;
    }
    //endregion

    public String toString(){
        return MIntInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+
            " operand="+operand
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitIntInsn(getOpcode(),getOperand());
    }
}
