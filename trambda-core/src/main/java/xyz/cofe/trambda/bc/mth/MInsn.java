package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

/**
 * Простая инструкция
 *
 * the opcode of the instruction to be visited. This opcode is either
 *
 * <table cellspacing="2" border="1" cellpadding="5" style="border-collapse: collapse">
 *     <tr>
 *         <th>Мнемоника</th> <th>Код</th> <th>Параметры</th> <th>Стек</th> <th>Описание</th>
 *     </tr>
 *
 *     <tr>
 *         <td>NOP</td> <td>00</td> <td></td> <td>[No change]</td> <td>perform no operation</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ACONST_NULL</td> <td>01</td> <td></td> <td>→ null</td> <td>push a null reference onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_M1</td> <td>02</td> <td></td> <td>→ -1</td> <td>load the int value −1 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_0</td> <td>03</td> <td></td> <td>→ 0</td> <td>load the int value 0 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_1</td> <td>04</td> <td></td> <td>→ 1</td> <td>load the int value 1 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_2</td> <td>05</td> <td></td> <td>→ 2</td> <td>load the int value 2 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_3</td> <td>06</td> <td></td> <td>→ 3</td> <td>load the int value 3 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_4</td> <td>07</td> <td></td> <td>→ 4</td> <td>load the int value 4 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ICONST_5</td> <td>08</td> <td></td> <td>→ 5</td> <td>load the int value 5 onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>LCONST_0</td> <td>09</td> <td></td> <td>→ 0L</td> <td>push 0L (the number zero with type long) onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>LCONST_1</td> <td>0a</td> <td></td> <td>→ 1L</td> <td>push 1L (the number one with type long) onto the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>FCONST_0</td> <td>0b</td> <td></td> <td>→ 0.0f</td> <td>push 0.0f on the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>FCONST_1</td> <td>0c</td> <td></td> <td>→ 1.0f</td> <td>push 1.0f on the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>FCONST_2</td> <td>0d</td> <td></td> <td>→ 2.0f</td> <td>push 2.0f on the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>DCONST_0</td> <td>0e</td> <td></td> <td>→ 0.0</td> <td>push 0.0 (double) on the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>DCONST_1</td> <td>0f</td> <td></td> <td>→ 1.0</td> <td>push 1.0 (double) on the stack</td>
 *     </tr>
 *
 *     <tr>
 *         <td>IALOAD</td> <td>2e</td> <td></td> <td>arrayref, index → value</td> <td>load an int from an array</td>
 *     </tr>
 *
 *     <tr>
 *         <td>LALOAD</td> <td>2f</td> <td></td> <td>arrayref, index → value</td> <td>load a long from an array</td>
 *     </tr>
 *
 *     <tr>
 *         <td>FALOAD</td> <td>30</td> <td></td> <td>arrayref, index → value</td> <td>load a float from an array</td>
 *     </tr>
 *     <tr>
 *         <td>DALOAD</td> <td>31</td> <td></td> <td>arrayref, index → value</td> <td>load a double from an array</td>
 *     </tr>
 *     <tr>
 *         <td>AALOAD</td> <td>32</td> <td></td> <td>arrayref, index → value</td> <td>load onto the stack a reference from an array</td>
 *     </tr>
 *     <tr>
 *         <td>BALOAD</td> <td>33</td> <td></td> <td>arrayref, index → value</td> <td>load a byte or Boolean value from an array</td>
 *     </tr>
 *     <tr>
 *         <td>CALOAD</td> <td>34</td> <td></td> <td>arrayref, index → value</td> <td>load a char from an array</td>
 *     </tr>
 *     <tr>
 *         <td>SALOAD</td> <td>35</td> <td></td> <td>arrayref, index → value</td> <td>load short from array</td>
 *     </tr>
 *
 *     <tr>
 *         <td>IASTORE</td> <td>4f</td> <td></td> <td>arrayref, index, value →</td> <td>store an int into an array</td>
 *     </tr>
 *     <tr>
 *         <td>LASTORE</td> <td>50</td> <td></td> <td>arrayref, index, value →</td> <td>store a long to an array</td>
 *     </tr>
 *     <tr>
 *         <td>FASTORE</td> <td>51</td> <td></td> <td>arrayref, index, value →</td> <td>store a float in an array</td>
 *     </tr>
 *     <tr>
 *         <td>DASTORE</td> <td>52</td> <td></td> <td>arrayref, index, value →</td> <td>store a double into an array</td>
 *     </tr>
 *     <tr>
 *         <td>AASTORE</td> <td>53</td> <td></td> <td>arrayref, index, value →</td> <td>store a reference in an array</td>
 *     </tr>
 *     <tr>
 *         <td>BASTORE</td> <td>54</td> <td></td> <td>arrayref, index, value →</td> <td>store a byte or Boolean value into an array</td>
 *     </tr>
 *
 *     <tr>
 *         <td>CASTORE</td> <td>55</td> <td></td> <td>arrayref, index, value →</td> <td>store a char into an array</td>
 *     </tr>
 *     <tr>
 *         <td>SASTORE</td> <td>56</td> <td></td> <td>arrayref, index, value →</td> <td>store short to array</td>
 *     </tr>
 *
 *     <tr>
 *         <td>POP</td> <td>57</td> <td></td> <td>value →</td> <td>discard the top value on the stack</td>
 *     </tr>
 *     <tr>
 *         <td>POP2</td> <td>58</td> <td></td> <td>{value2, value1} →</td> <td>discard the top two values on the stack (or one value, if it is a double or long)</td>
 *     </tr>
 *
 *     <tr>
 *         <td>DUP</td> <td>59</td> <td></td> <td>value → value, value</td> <td>duplicate the value on top of the stack</td>
 *     </tr>
 *     <tr>
 *         <td>DUP_X1</td> <td>5a</td> <td></td> <td>value2, value1 → value1, value2, value1</td> <td>insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long.</td>
 *     </tr>
 *     <tr>
 *         <td>DUP_X2</td> <td>5b</td> <td></td> <td>value3, value2, value1 → value1, value3, value2, value1</td> <td>insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top</td>
 *     </tr>
 *     <tr>
 *         <td>DUP2</td> <td>5c</td> <td></td> <td>{value2, value1} → {value2, value1}, {value2, value1}</td> <td>duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)</td>
 *     </tr>
 *     <tr>
 *         <td>DUP2_X1</td> <td>5d</td> <td></td> <td>value3, {value2, value1} → {value2, value1}, value3, {value2, value1}</td> <td>duplicate two words and insert beneath third word (see explanation above)</td>
 *     </tr>
 *     <tr>
 *         <td>DUP2_X2</td> <td>5e</td> <td></td> <td>{value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1}</td> <td>duplicate two words and insert beneath fourth word</td>
 *     </tr>
 *
 *     <tr>
 *         <td>SWAP</td> <td>5f</td> <td></td> <td>value2, value1 → value1, value2</td> <td>swaps two top words on the stack (note that value1 and value2 must not be double or long)</td>
 *     </tr>
 *
 *     <tr>
 *         <td>IADD</td> <td>60</td> <td></td> <td>value1, value2 → result</td> <td>add two ints</td>
 *     </tr>
 *     <tr>
 *         <td>LADD</td> <td>61</td> <td></td> <td>value1, value2 → result</td> <td>add two longs</td>
 *     </tr>
 *     <tr>
 *         <td>FADD</td> <td>62</td> <td></td> <td>value1, value2 → result</td> <td>add two floats</td>
 *     </tr>
 *     <tr>
 *         <td>DADD</td> <td>63</td> <td></td> <td>value1, value2 → result</td> <td>add two doubles</td>
 *     </tr>
 *
 *     <tr>
 *         <td>ISUB</td> <td>64</td> <td></td> <td>value1, value2 → result</td> <td>int subtract</td>
 *     </tr>
 *     <tr>
 *         <td>LSUB</td> <td>65</td> <td></td> <td>value1, value2 → result</td> <td>subtract two longs</td>
 *     </tr>
 *     <tr>
 *         <td>FSUB</td> <td>66</td> <td></td> <td>value1, value2 → result</td> <td>subtract two floats</td>
 *     </tr>
 *     <tr>
 *         <td>DSUB</td> <td>67</td> <td></td> <td>value1, value2 → result</td> <td>subtract a double from another</td>
 *     </tr>
 *
 *     <tr>
 *         <td>IMUL</td> <td>68</td> <td></td> <td>value1, value2 → result</td> <td>multiply two integers</td>
 *     </tr>
 *     <tr>
 *         <td>LMUL</td> <td>69</td> <td></td> <td>value1, value2 → result</td> <td>multiply two longs</td>
 *     </tr>
 *     <tr>
 *         <td>FMUL</td> <td>6a</td> <td></td> <td>value1, value2 → result</td> <td>multiply two floats</td>
 *     </tr>
 *     <tr>
 *         <td>DMUL</td> <td>6b</td> <td></td> <td>value1, value2 → result</td> <td>multiply two doubles</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>IDIV</td> <td>6c</td> <td></td> <td>value1, value2 → result</td> <td>divide two integers</td>
 *     </tr>
 *     <tr>
 *         <td>LDIV</td> <td>6d</td> <td></td> <td>value1, value2 → result</td> <td>divide two longs</td>
 *     </tr>
 *     <tr>
 *         <td>FDIV</td> <td>6e</td> <td></td> <td>value1, value2 → result</td> <td>divide two floats</td>
 *     </tr>
 *     <tr>
 *         <td>DDIV</td> <td>6f</td> <td></td> <td>value1, value2 → result</td> <td>divide two doubles</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>IREM</td> <td>70</td> <td></td> <td>value1, value2 → result</td> <td>logical int remainder</td>
 *     </tr>
 *     <tr>
 *         <td>LREM</td> <td>71</td> <td></td> <td>value1, value2 → result</td> <td>remainder of division of two longs</td>
 *     </tr>
 *     <tr>
 *         <td>FREM</td> <td>72</td> <td></td> <td>value1, value2 → result</td> <td>get the remainder from a division between two floats</td>
 *     </tr>
 *     <tr>
 *         <td>DREM</td> <td>73</td> <td></td> <td>value1, value2 → result</td> <td>get the remainder from a division between two doubles</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>INEG</td> <td>74</td> <td></td> <td>value → result</td> <td>negate int</td>
 *     </tr>
 *     <tr>
 *         <td>LNEG</td> <td>75</td> <td></td> <td>value → result</td> <td>negate a long</td>
 *     </tr>
 *     <tr>
 *         <td>FNEG</td> <td>76</td> <td></td> <td>value → result</td> <td>negate a float</td>
 *     </tr>
 *     <tr>
 *         <td>DNEG</td> <td>74</td> <td></td> <td>value → result</td> <td>negate a double</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>ISHL</td> <td>78</td> <td></td> <td>value1, value2 → result</td> <td>int shift left</td>
 *     </tr>
 *     <tr>
 *         <td>LSHL</td> <td>79</td> <td></td> <td>value1, value2 → result</td> <td>bitwise shift left of a long value1 by int value2 positions</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>ISHR</td> <td>7a</td> <td></td> <td>value1, value2 → result</td> <td>int arithmetic shift right</td>
 *     </tr>
 *     <tr>
 *         <td>LSHR</td> <td>7b</td> <td></td> <td>value1, value2 → result</td> <td>bitwise shift right of a long value1 by int value2 positions</td>
 *     </tr>
 *     <tr>
 *         <td>IUSHR</td> <td>7c</td> <td></td> <td>value1, value2 → result</td> <td>int logical shift right</td>
 *     </tr>
 *     <tr>
 *         <td>LUSHR</td> <td>7d</td> <td></td> <td>value1, value2 → result</td> <td>bitwise shift right of a long value1 by int value2 positions, unsigned</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>IAND</td> <td>7e</td> <td></td> <td>value1, value2 → result</td> <td>perform a bitwise AND on two integers</td>
 *     </tr>
 *     <tr>
 *         <td>LAND</td> <td>7f</td> <td></td> <td>value1, value2 → result</td> <td>bitwise AND of two longs</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>IOR</td> <td>80</td> <td></td> <td>value1, value2 → result</td> <td>bitwise int OR</td>
 *     </tr>
 *     <tr>
 *         <td>LOR</td> <td>81</td> <td></td> <td>value1, value2 → result</td> <td>bitwise OR of two longs</td>
 *     </tr>
 *     <tr>
 *         <td>IXOR</td> <td>82</td> <td></td> <td>value1, value2 → result</td> <td>int xor</td>
 *     </tr>
 *     <tr>
 *         <td>LXOR</td> <td>83</td> <td></td> <td>value1, value2 → result</td> <td>bitwise XOR of two longs</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>I2L</td> <td>85</td> <td></td> <td>value → result</td> <td>convert an int into a long</td>
 *     </tr>
 *     <tr>
 *         <td>I2F</td> <td>86</td> <td></td> <td>value → result</td> <td>convert an int into a float</td>
 *     </tr>
 *     <tr>
 *         <td>I2D</td> <td>87</td> <td></td> <td>value → result</td> <td>convert an int into a double</td>
 *     </tr>
 *     <tr>
 *         <td>L2I</td> <td>88</td> <td></td> <td>value → result</td> <td>convert an long into a int</td>
 *     </tr>
 *     <tr>
 *         <td>L2F</td> <td>89</td> <td></td> <td>value → result</td> <td>convert an long into a float</td>
 *     </tr>
 *     <tr>
 *         <td>L2D</td> <td>8a</td> <td></td> <td>value → result</td> <td>convert an long into a double</td>
 *     </tr>
 *     <tr>
 *         <td>F2I</td> <td>8b</td> <td></td> <td>value → result</td> <td>convert an float into a int</td>
 *     </tr>
 *     <tr>
 *         <td>F2L</td> <td>8c</td> <td></td> <td>value → result</td> <td>convert an float into a long</td>
 *     </tr>
 *     <tr>
 *         <td>F2D</td> <td>8d</td> <td></td> <td>value → result</td> <td>convert an float into a double</td>
 *     </tr>
 *     <tr>
 *         <td>D2I</td> <td>8e</td> <td></td> <td>value → result</td> <td>convert an double into a int</td>
 *     </tr>
 *     <tr>
 *         <td>D2L</td> <td>8f</td> <td></td> <td>value → result</td> <td>convert an double into a long</td>
 *     </tr>
 *     <tr>
 *         <td>D2F</td> <td>90</td> <td></td> <td>value → result</td> <td>convert an double into a float</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>I2B</td> <td>91</td> <td></td> <td>value → result</td> <td>convert an int into a byte</td>
 *     </tr>
 *     <tr>
 *         <td>I2C</td> <td>92</td> <td></td> <td>value → result</td> <td>convert an int into a character</td>
 *     </tr>
 *     <tr>
 *         <td>I2S</td> <td>93</td> <td></td> <td>value → result</td> <td>convert an int into a short</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>LCMP</td> <td>94</td> <td></td> <td>value1, value2 → result</td> <td>push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise</td>
 *     </tr>
 *     <tr>
 *         <td>FCMPL</td> <td>95</td> <td></td> <td>value1, value2 → result</td> <td>compare two floats, -1 on NaN</td>
 *     </tr>
 *     <tr>
 *         <td>FCMPG</td> <td>96</td> <td></td> <td>value1, value2 → result</td> <td>compare two floats, 1 on NaN</td>
 *     </tr>
 *     <tr>
 *         <td>DCMPL</td> <td>97</td> <td></td> <td>value1, value2 → result</td> <td>compare two doubles, -1 on NaN</td>
 *     </tr>
 *     <tr>
 *         <td>DCMPG</td> <td>98</td> <td></td> <td>value1, value2 → result</td> <td>compare two doubles, 1 on NaN</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>IRETURN</td> <td>ac</td> <td></td> <td>value → [empty]</td> <td>return an integer from a method</td>
 *     </tr>
 *     <tr>
 *         <td>LRETURN</td> <td>ad</td> <td></td> <td>value → [empty]</td> <td>return a long value</td>
 *     </tr>
 *     <tr>
 *         <td>FRETURN</td> <td>ae</td> <td></td> <td>value → [empty]</td> <td>return a float</td>
 *     </tr>
 *     <tr>
 *         <td>DRETURN</td> <td>af</td> <td></td> <td>value → [empty]</td> <td>return a double from a method</td>
 *     </tr>
 *     <tr>
 *         <td>ARETURN</td> <td>b0</td> <td></td> <td>objectref → [empty]</td> <td>return a reference from a method</td>
 *     </tr>
 *     <tr>
 *         <td>RETURN</td> <td>b1</td> <td></td> <td>→ [empty]</td> <td>return void from method</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>ARRAYLENGTH</td> <td>be</td> <td></td> <td>arrayref → length</td> <td>get the length of an array</td>
 *     </tr>
 *     <tr>
 *         <td>ATHROW</td> <td>bf</td> <td></td> <td>objectref → [empty], objectref</td> <td>throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)</td>
 *     </tr>
 * 
 *     <tr>
 *         <td>MONITORENTER</td> <td>c2</td> <td></td> <td>objectref → </td> <td>enter monitor for object ("grab the lock" – start of synchronized() section)</td>
 *     </tr>
 *     <tr>
 *         <td>MONITOREXIT</td> <td>c3</td> <td></td> <td>objectref → </td> <td>exit monitor for object ("release the lock" – end of synchronized() section)</td>
 *     </tr>
 * </table>
 */
public class MInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MInsn(){}
    public MInsn(int op){this.opcode = op;}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MInsn(MInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.opcode = sample.getOpcode();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MInsn clone(){ return new MInsn(this); }

    //region opcode
    private int opcode;

    public int getOpcode(){
        return opcode;
    }

    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion

    public String toString(){
        return MInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitInsn(getOpcode());
    }
}
