package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * multianewarray
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Create new multidimensional array
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * multianewarray
 * indexbyte1
 * indexbyte2
 * dimensions
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * multianewarray = 197 (0xc5)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., count1, [count2, ...] →
 * ..., arrayref
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * The dimensions operand is an unsigned byte that must be greater than or equal to 1. It represents the number of dimensions of the array to be created. The operand stack must contain dimensions values. Each such value represents the number of components in a dimension of the array to be created, must be of type int, and must be non-negative. The count1 is the desired length in the first dimension, count2 in the second, etc.
 *
 * <p>
 * All of the count values are popped off the operand stack. The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at the index must be a symbolic reference to a class, array, or interface type. The named class, array, or interface type is resolved (§5.4.3.1). The resulting entry must be an array class type of dimensionality greater than or equal to dimensions.
 *
 * <p>
 * A new multidimensional array of the array type is allocated from the garbage-collected heap. If any count value is zero, no subsequent dimensions are allocated. The components of the array in the first dimension are initialized to subarrays of the type of the second dimension, and so on. The components of the last allocated dimension of the array are initialized to the default initial value (§2.3, §2.4) for the element type of the array type. A reference arrayref to the new array is pushed onto the operand stack.
 * <h2 style="font-weight: bold">Linking Exceptions</h2>
 *
 * During resolution of the symbolic reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be thrown.
 *
 * <p>
 * Otherwise, if the current class does not have permission to access the element type of the resolved array class, multianewarray throws an IllegalAccessError.
 *
 * <h2 style="font-weight: bold">Run-time Exception</h2>
 *
 * Otherwise, if any of the dimensions values on the operand stack are less than zero, the multianewarray instruction throws a NegativeArraySizeException.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * It may be more efficient to use newarray or anewarray (§newarray, §anewarray) when creating an array of a single dimension.
 *
 * <p>
 * The array class referenced via the run-time constant pool may have more dimensions than the dimensions operand of the multianewarray instruction. In that case, only the first dimensions of the dimensions of the array are created.
 */
public class MMultiANewArrayInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MMultiANewArrayInsn(){
    }
    public MMultiANewArrayInsn(String descriptor, int numDimensions){
        this.descriptor = descriptor;
        this.numDimensions = numDimensions;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MMultiANewArrayInsn(MMultiANewArrayInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        descriptor = sample.descriptor;
        numDimensions = sample.numDimensions;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MMultiANewArrayInsn clone(){ return new MMultiANewArrayInsn(this); }

    //region descriptor
    private String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region numDimensions
    private int numDimensions;

    public int getNumDimensions(){
        return numDimensions;
    }

    public void setNumDimensions(int numDimensions){
        this.numDimensions = numDimensions;
    }
    //endregion

    public String toString(){
        return MMultiANewArrayInsn.class.getSimpleName()+
            " descriptor="+descriptor+
            " numDimensions="+numDimensions;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitMultiANewArrayInsn(getDescriptor(),getNumDimensions());
    }
}
