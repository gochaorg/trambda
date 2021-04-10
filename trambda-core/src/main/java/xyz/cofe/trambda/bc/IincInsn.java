package xyz.cofe.trambda.bc;

/**
 * Increment local variable by constant (<a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html">jvm spec</a>).
 *
 * <h2 style="font-weight: bold">Operation</h2>
 * Increment local variable by constant
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * iinc
 * index
 * const
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * iinc = 132 (0x84)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 * No change
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The const is an immediate signed byte.
 * The local variable at index must contain an int.
 * The value const is first sign-extended to an int, and then the local variable at index is incremented by that amount.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The iinc opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index and to increment it by a two-byte immediate signed value.
 */
public class IincInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public IincInsn(){}
    public IincInsn(int variable, int increment){
        this.variable = variable;
        this.increment = increment;
    }

    //region variable
    private int variable;
    public int getVariable(){
        return variable;
    }
    public void setVariable(int variable){
        this.variable = variable;
    }
    //endregion
    //region increment
    private int increment;
    public int getIncrement(){ return increment; }
    public void setIncrement(int increment){ this.increment = increment; }
    //endregion

    public String toString(){
        return "IincInsn variable="+variable+" increment="+increment;
    }
}
