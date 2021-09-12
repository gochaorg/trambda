package xyz.cofe.trambda.bc.mth;

import java.util.Optional;

public enum OpCode {
    /**
     * perform no operation
     * <p>Стек [No change]
     */
    NOP(0),

    /**
     * push a null reference onto the stack
     * <p>Стек → null
     */
    ACONST_NULL(1), 
    
    /**
     * load the int value −1 onto the stack
     * <p>Стек → -1
     */
    ICONST_M1(2), 

    /**
     * load the int value 0 onto the stack
     * <p>Стек → 0
     */
    ICONST_0(3), 

    /**
     * load the int value 1 onto the stack
     * <p>Стек → 1
     */
    ICONST_1(4), 

    /**
     * load the int value 2 onto the stack
     * <p>Стек → 2
     */
    ICONST_2(5), 

    /**
     * load the int value 3 onto the stack
     * <p>Стек → 3
     */
    ICONST_3(6), 

    /**
     * load the int value 4 onto the stack
     * <p>Стек → 4
     */
    ICONST_4(7), 

    /**
     * load the int value 5 onto the stack
     * <p>Стек → 5
     */
    ICONST_5(8), 

    /**
     * push 0L (the number zero with type long) onto the stack
     * <p>Стек → 0
     */
    LCONST_0(9), 

    /**
     * push 1L (the number one with type long) onto the stack
     * <p>Стек → 1
     */
    LCONST_1(10), 

    /**
     * push 0.0f on the stack
     * <p>Стек → 0f
     */
    FCONST_0(11), 

    /**
     * push 1.0f on the stack
     * <p>Стек → 1f
     */
    FCONST_1(12), 

    /**
     * push 2.0f on the stack
     * <p>Стек → 2f
     */
    FCONST_2(13), 

    /**
     * push 0.0 (double) on the stack
     * <p>Стек → 0.0
     */
    DCONST_0(14), 

    /**
     * push 1.0 (double) on the stack
     * <p>Стек → 1.0
     */
    DCONST_1(15), 

    /**
     * push a byte onto the stack as an integer value
     * <p>Параметры 1: byte
     * <p>Стек → value
     */
    BIPUSH(16), // visitIntInsn

    /**
     * push a short onto the stack as an integer value
     * <p>Параметры 2: byte1, byte2
     * <p>Стек → value
     */
    SIPUSH(17), 

    /**
     * push a constant #index from a constant pool 
     * (String, int, float, Class, java.lang.invoke.MethodType, 
     * java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack
     * <p>Параметры 1: index
     * <p>Стек → value
     */
    LDC(18), // visitLdcInsn
    
    /**
     * push a constant #index from a constant pool 
     * (String, int, float, Class, java.lang.invoke.MethodType, 
     * java.lang.invoke.MethodHandle, or a dynamically-computed constant) 
     * onto the stack (wide index is constructed as indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек → value
     */
    LDC_W(19),

    /**
     * push a constant #index from a constant pool (double, long, or a 
     * dynamically-computed constant) 
     * onto the stack (wide index is constructed as indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек → value
     */
    LDC2_W(20),

    /**
     * load an int value from a local variable #index
     * <p>Параметры 1: index
     * <p>Стек → value
     */
    ILOAD(21), // visitVarInsn

    /**
     * load a long value from a local variable #index
     * <p>Параметры 1: index
     * <p>Стек → value
     */
    LLOAD(22), 

    /**
     * load a float value from a local variable #index
     * <p>Параметры 1: index
     * <p>Стек → value
     */
    FLOAD(23), 

    /**
     * load a double value from a local variable #index
     * <p>Параметры 1: index
     * <p>Стек → value
     */
    DLOAD(24), 

    /**
     * load a reference onto the stack from a local variable #index
     * <p>Параметры 1: index
     * <p>Стек → objectref
     */
    ALOAD(25), 

    /**
     * load an int from an array
     * <p>Стек arrayref, index → value
     */
    IALOAD(46), // visitInsn

    /**
     * load a long from an array
     * <p>Стек arrayref, index → value
     */
    LALOAD(47), 

    /**
     * load a float from an array
     * <p>Стек arrayref, index → value
     */
    FALOAD(48), 

    /**
     * load a double from an array
     * <p>Стек arrayref, index → value
     */
    DALOAD(49), 

    /**
     * load onto the stack a reference from an array
     * <p>Стек arrayref, index → value
     */
    AALOAD(50), 

    /**
     * load a byte or Boolean value from an array
     * <p>Стек arrayref, index → value
     */
    BALOAD(51), 

    /**
     * load a char from an array
     * <p>Стек arrayref, index → value
     */
    CALOAD(52), 

    /**
     * load short from array
     * <p>Стек arrayref, index → value
     */
    SALOAD(53), 

    /**
     * load a reference onto the stack from a local variable #index
     * <p>Параметры 1: index
     * <p>Стек → objectref
     */
    ISTORE(54), // visitVarInsn

    /**
     * store a long value in a local variable #index
     * <p>Параметры 1: index
     * <p>Стек value →
     */
    LSTORE(55), 

    /**
     * store a long value in a local variable 0
     * <p>Стек value →
     */
    LSTORE_0(0x3f), 

    /**
     * store a long value in a local variable 1
     * <p>Стек value →
     */
    LSTORE_1(0x40), 

    /**
     * store a long value in a local variable 2
     * <p>Стек value →
     */
    LSTORE_2(0x41), 

    /**
     * store a long value in a local variable 3
     * <p>Стек value →
     */
    LSTORE_3(0x42), 

    /**
     * store a float value into a local variable #index
     * <p>Параметры 1: index
     * <p>Стек value →
     */
    FSTORE(56), 

    /**
     * store a float value into local variable 0
     * <p>Стек value →
     */
    FSTORE_0(0x43), 

    /**
     * store a float value into local variable 1
     * <p>Стек value →
     */
    FSTORE_1(0x44), 

    /**
     * store a float value into local variable 2
     * <p>Стек value →
     */
    FSTORE_2(0x45), 

    /**
     * store a float value into local variable 3
     * <p>Стек value →
     */
    FSTORE_3(0x46), 

    /**
     * store a double value into a local variable #index
     * <p>Параметры 1: index
     * <p>Стек value →
     */
    DSTORE(57),

    /**
     * store a double into local variable 0
     * <p>Параметры 1: index
     * <p>Стек value →
     */
    DSTORE_0(0x47),

    /**
     * store a double into local variable 1
     * <p>Параметры 
     * <p>Стек value →
     */
    DSTORE_1(0x48),

    /**
     * store a double into local variable 2
     * <p>Стек value →
     */
    DSTORE_2(0x49),

    /**
     * store a double into local variable 3
     * <p>Стек value →
     */
    DSTORE_3(0x49),

    /**
     * store a reference into a local variable #index
     * <p>Параметры 1: index
     * <p>Стек objectref →
     */
    ASTORE(58), 

    /**
     * store a reference into local variable 0
     * <p>Стек objectref →
     */
    ASTORE_0(0x4b), 

    /**
     * store a reference into local variable 1
     * <p>Стек objectref →
     */
    ASTORE_1(0x4c), 

    /**
     * store a reference into local variable 2
     * <p>Стек objectref →
     */
    ASTORE_2(0x4d), 

    /**
     * store a reference into local variable 3
     * <p>Стек objectref →
     */
    ASTORE_3(0x4e), 

    /**
     * store an int into an array
     * <p>Стек arrayref, index, value →
     */
    IASTORE(79), // visitInsn

    /**
     * store a long to an array
     * <p>Стек arrayref, index, value →
     */
    LASTORE(80), 

    /**
     * store a float in an array
     * <p>Стек arrayref, index, value →
     */
    FASTORE(81), 

    /**
     * store a double into an array
     * <p>Стек arrayref, index, value →
     */
    DASTORE(82), 

    /**
     * store a reference in an array
     * <p>Стек arrayref, index, value →
     */
    AASTORE(83), 

    /**
     * store a byte or Boolean value into an array
     * <p>Стек arrayref, index, value →
     */
    BASTORE(84), 

    /**
     * store a char into an array
     * <p>Стек arrayref, index, value →
     */
    CASTORE(85), 

    /**
     * store short to array
     * <p>Стек arrayref, index, value →
     */
    SASTORE(86), 

    /**
     * discard the top value on the stack
     * <p>Стек value →
     */
    POP(87), 

    /**
     * discard the top two values on the stack (or one value, if it is a double or long)
     * <p>Стек {value2, value1} →
     */
    POP2(88), 

    /**
     * duplicate the value on top of the stack
     * <p>Стек value → value, value
     */
    DUP(89), 

    /**
     * insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long.
     * <p>Стек value2, value1 → value1, value2, value1
     */
    DUP_X1(90), 

    /**
     * insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top
     * <p>Стек value3, value2, value1 → value1, value3, value2, value1
     */
    DUP_X2(91), 

    /**
     * duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)
     * <p>Стек {value2, value1} → {value2, value1}, {value2, value1}
     */
    DUP2(92), 

    /**
     * duplicate two words and insert beneath third word (see explanation above)
     * <p>Стек value3, {value2, value1} → {value2, value1}, value3, {value2, value1}
     */
    DUP2_X1(93), 

    /**
     * duplicate two words and insert beneath fourth word
     * <p>Стек {value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1}
     */
    DUP2_X2(94), 

    /**
     * swaps two top words on the stack (note that value1 and value2 must not be double or long)
     * <p>Стек value2, value1 → value1, value2
     */
    SWAP(95), 

    /**
     * add two ints
     * <p>Стек value1, value2 → result
     */
    IADD(96), 

    /**
     * add two longs
     * <p>Стек value1, value2 → result
     */
    LADD(97), 

    /**
     * add two floats
     * <p>Стек value1, value2 → result
     */
    FADD(98), 

    /**
     * add two doubles
     * <p>Стек value1, value2 → result
     */
    DADD(99), 

    /**
     * int subtract
     * <p>Стек value1, value2 → result
     */
    ISUB(100), 

    /**
     * subtract two longs
     * <p>Стек value1, value2 → result
     */
    LSUB(101), 

    /**
     * subtract two floats
     * <p>Стек value1, value2 → result
     */
    FSUB(102), 

    /**
     * subtract a double from another
     * <p>Стек value1, value2 → result
     */
    DSUB(103), 

    /**
     * multiply two integers
     * <p>Стек value1, value2 → result
     */
    IMUL(104), 

    /**
     * multiply two longs
     * <p>Стек value1, value2 → result
     */
    LMUL(105), 

    /**
     * multiply two floats
     * <p>Стек value1, value2 → result
     */
    FMUL(106), 

    /**
     * multiply two doubles
     * <p>Стек value1, value2 → result
     */
    DMUL(107), 

    /**
     * divide two integers
     * <p>Стек value1, value2 → result
     */
    IDIV(108), 

    /**
     * divide two longs
     * <p>Стек value1, value2 → result
     */
    LDIV(109), 

    /**
     * divide two floats
     * <p>Стек value1, value2 → result
     */
    FDIV(110), 

    /**
     * divide two doubles
     * <p>Стек value1, value2 → result
     */
    DDIV(111), 

    /**
     * logical int remainder
     * <p>Стек value1, value2 → result
     */
    IREM(112), 

    /**
     * remainder of division of two longs
     * <p>Стек value1, value2 → result
     */
    LREM(113), 

    /**
     * get the remainder from a division between two floats
     * <p>Стек value1, value2 → result
     */
    FREM(114), 

    /**
     * get the remainder from a division between two doubles
     * <p>Стек value1, value2 → result
     */
    DREM(115), 

    /**
     * negate int
     * <p>Стек value → result
     */
    INEG(116), 

    /**
     * negate a long
     * <p>Стек value → result
     */
    LNEG(117), 

    /**
     * negate a float
     * <p>Стек value → result
     */
    FNEG(118), 

    /**
     * negate a double
     * <p>Стек value → result
     */
    DNEG(119), 

    /**
     * int shift left
     * <p>Стек value1, value2 → result
     */
    ISHL(120), 

    /**
     * bitwise shift left of a long value1 by int value2 positions
     * <p>Стек value1, value2 → result
     */
    LSHL(121), 

    /**
     * int arithmetic shift right
     * <p>Стек value1, value2 → result
     */
    ISHR(122), 

    /**
     * bitwise shift right of a long value1 by int value2 positions
     * <p>Стек value1, value2 → result
     */
    LSHR(123), 

    /**
     * int logical shift right
     * <p>Стек value1, value2 → result
     */
    IUSHR(124), 

    /**
     * bitwise shift right of a long value1 by int value2 positions, unsigned
     * <p>Стек value1, value2 → result
     */
    LUSHR(125), 

    /**
     * perform a bitwise AND on two integers
     * <p>Стек value1, value2 → result
     */
    IAND(126), 

    /**
     * bitwise AND of two longs
     * <p>Стек value1, value2 → result
     */
    LAND(127), 

    /**
     * bitwise int OR
     * <p>Стек value1, value2 → result
     */
    IOR(128), 

    /**
     * bitwise OR of two longs
     * <p>Стек value1, value2 → result
     */
    LOR(129), 

    /**
     * int xor
     * <p>Стек value1, value2 → result
     */
    IXOR(130), 

    /**
     * bitwise XOR of two longs
     * <p>Стек value1, value2 → result
     */
    LXOR(131), 

    /**
     * increment local variable #index by signed byte const
     * <p>Параметры 2: index, const
     * <p>Стек [No change]
     */
    IINC(132), // visitIincInsn

    /**
     * convert an int into a long
     * <p>Стек value → result
     */
    I2L(133), // visitInsn

    /**
     * convert an int into a float
     * <p>Стек value → result
     */
    I2F(134), 

    /**
     * convert an int into a double
     * <p>Стек value → result
     */
    I2D(135), 

    /**
     * convert an long into a int
     * <p>Стек value → result
     */
    L2I(136), 

    /**
     * convert an long into a float
     * <p>Стек value → result
     */
    L2F(137), 

    /**
     * convert an long into a double
     * <p>Стек value → result
     */
    L2D(138), 

    /**
     * convert an float into a int
     * <p>Стек value → result
     */
    F2I(139), 

    /**
     * convert an float into a long
     * <p>Стек value → result
     */
    F2L(140), 

    /**
     * convert an float into a double
     * <p>Стек value → result
     */
    F2D(141), 

    /**
     * convert an double into a int
     * <p>Стек value → result
     */
    D2I(142), 

    /**
     * convert an double into a long
     * <p>Стек value → result
     */
    D2L(143), 

    /**
     * convert an double into a float
     * <p>Стек value → result
     */
    D2F(144), 

    /**
     * convert an int into a byte
     * <p>Стек value → result
     */
    I2B(145), 

    /**
     * convert an int into a character
     * <p>Стек value → result
     */
    I2C(146), 

    /**
     * convert an int into a short
     * <p>Стек value → result
     */
    I2S(147), 

    /**
     * push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise
     * <p>Стек value1, value2 → result
     */
    LCMP(148), 

    /**
     * compare two floats, -1 on NaN
     * <p>Стек value1, value2 → result
     */
    FCMPL(149), 

    /**
     * compare two floats, 1 on NaN
     * <p>Стек value1, value2 → result
     */
    FCMPG(150), 

    /**
     * compare two doubles, -1 on NaN
     * <p>Стек value1, value2 → result
     */
    DCMPL(151), 

    /**
     * compare two doubles, 1 on NaN
     * <p>Стек value1, value2 → result
     */
    DCMPG(152), 
    
    /**
     * if value is 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     */
    IFEQ(153), // visitJumpInsn

    /**
     * if value is not 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     */
    IFNE(154), 

    /**
     * if value is less than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     */
    IFLT(155), 

    /**
     * if value is greater than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     */
    IFGE(156), 

    /**
     * if value is greater than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     */
    IFGT(157), 

    /**
     * if value is less than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     */
    IFLE(158), 

    /**
     * if ints are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ICMPEQ(159), 

    /**
     * if ints are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ICMPNE(160), 

    /**
     * if value1 is less than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ICMPLT(161), 

    /**
     * if value1 is greater than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ICMPGE(162), 

    /**
     * if value1 is greater than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ICMPGT(163), 

    /**
     * if value1 is less than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ICMPLE(164), 

    /**
     * if references are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ACMPEQ(165), 

    /**
     * if references are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value1, value2 →
     */
    IF_ACMPNE(166), 

    /**
     * goes to another instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек [no change]
     */
    GOTO(167), 
    
    /**
     * goes to another instruction at branchoffset (signed int constructed from unsigned bytes branchbyte1 &lt;&lt; 24 | branchbyte2 &lt;&lt; 16 | branchbyte3 &lt;&lt; 8 | branchbyte4)
     * <p>Параметры 4: branchbyte1, branchbyte2, branchbyte3, branchbyte4
     * <p>Стек [no change]
     */
    GOTO_W(0xc8),

    /**
     * jump to subroutine at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2) and place the return address on the stack
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек → address
     * <hr>
     * <b>Description</b>
     * <p> The address of the opcode of the instruction immediately following this jsr instruction is pushed onto the operand stack as a value of type returnAddress. The unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, where the offset is (branchbyte1 &lt;&lt; 8) | branchbyte2. Execution proceeds at that offset from the address of this jsr instruction. The target address must be that of an opcode of an instruction within the method that contains this jsr instruction.
     * <p> <b>Notes</b>
     * <p> Note that jsr pushes the address onto the operand stack and ret (§ret) gets it out of a local variable. This asymmetry is intentional.
     * <p> In Oracle's implementation of a compiler for the Java programming language prior to Java SE 6, the jsr instruction was used with the ret instruction in the implementation of the finally clause (§3.13, §4.10.2.5).
     * 
     * <hr>
     * <b>Описание</b>
     * <p> Адрес кода операции инструкции, следующей сразу за этой инструкцией jsr, помещается в стек операндов как значение типа returnAddress. Беззнаковые branchbyte1 и branchbyte2 используются для создания подписанного 16-битного смещения, где смещение равно (branchbyte1 &lt;&lt; 8) | branchbyte2. Выполнение продолжается с этого смещения от адреса этой инструкции jsr. Целевой адрес должен соответствовать коду операции инструкции в методе, который содержит эту инструкцию jsr.
     * <p> <b>Примечания</b>
     * <p> Обратите внимание, что jsr помещает адрес в стек операндов, а ret (§ret) получает его из локальной переменной. Эта асимметрия преднамеренная.
     * <p> В реализации Oracle компилятора для языка программирования Java до Java SE 6 инструкция jsr использовалась с инструкцией ret в реализации предложения finally (§3.13, §4.10.2.5).
     */
    JSR(168), 

    /**
     * continue execution from address taken from a local variable #index (the asymmetry with jsr is intentional)
     * <p>Параметры 1: index
     * <p>Стек [No change]
 * <hr>
 * Обратите внимание, что jsr (§jsr) помещает адрес в стек операндов, а ret получает его из локальной переменной. Эта асимметрия преднамеренная.
 * <p> В реализации Oracle компилятора для языка программирования Java до Java SE 6 инструкция ret использовалась с инструкциями jsr и jsr_w (§jsr, §jsr_w) в реализации предложения finally (§3.13, §4.10.2.5) ).
 * <p> Инструкцию ret не следует путать с инструкцией return (§return). Команда возврата возвращает управление от метода вызывающей стороне, не передавая никакого значения обратно вызывающей стороне.
 * <p> Код операции ret может использоваться вместе с инструкцией wide (§wide) для доступа к локальной переменной с использованием двухбайтового беззнакового индекса.
     */
    RET(169), // visitVarInsn


    /**
     * continue execution from an address in the table at offset index
     * <p>Параметры 16+: [0–3 bytes padding], defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4, jump offsets...
     * <p>Стек index →
     */
    TABLESWITCH(170), // visiTableSwitchInsn


    /**
     * a target address is looked up from a table using a key and execution continues from the instruction at that address
     * <p>Параметры 8+: &lt;0 - 3 bytes padding&gt;, defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, npairs1, npairs2, npairs3, npairs4, match-offset pairs...
     * <p>Стек key →
     */
    LOOKUPSWITCH(171), // visitLookupSwitch
    
    /**
     * return an integer from a method
     * <p>Стек value → [empty]
     */
    IRETURN(172), // visitInsn

    /**
     * return a long value
     * <p>Стек value → [empty]
     */
    LRETURN(173), 

    /**
     * return a float
     * <p>Стек value → [empty]
     */
    FRETURN(174), 

    /**
     * return a double from a method
     * <p>Стек value → [empty]
     */
    DRETURN(175), 

    /**
     * return a reference from a method
     * <p>Стек objectref → [empty]
     */
    ARETURN(176), 

    /**
     * return void from method
     * <p>Стек → [empty]
     */
    RETURN(177), 

    /**
     * get a static field value of a class, where the field is identified by field reference in the constant pool index (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек → value
     */
    GETSTATIC(178), // visitFieldInsn

    /**
     * set static field to value in a class, where the field is identified by a field reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек value →
     */
    PUTSTATIC(179), 

    /**
     * get a field value of an object objectref, where the field is identified by field reference in the constant pool index (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек objectref → value
     */
    GETFIELD(180), 

    /**
     * set field to value in an object objectref, where the field is identified by a field reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек objectref, value →
     */
    PUTFIELD(181), 

    /**
     * invoke virtual method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек objectref, [arg1, arg2, ...] → result
     */
    INVOKEVIRTUAL(182), // visitMethodInsn

    /**
     * invoke instance method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек objectref, [arg1, arg2, ...] → result
     */
    INVOKESPECIAL(183), 

    /**
     * invoke a static method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек [arg1, arg2, ...] → result
     */
    INVOKESTATIC(184), 

    /**
     * invokes an interface method on object objectref and puts the result on the stack (might be void); the interface method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 4: indexbyte1, indexbyte2, count, 0
     * <p>Стек objectref, [arg1, arg2, ...] → result
     */
    INVOKEINTERFACE(185), 
    
    /**
     * invokes a dynamic method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 4: indexbyte1, indexbyte2, 0, 0
     * <p>Стек [arg1, arg2, ...] → result
     */
    INVOKEDYNAMIC(186), // visitInvokeDynamicInsn

    /**
     * create new object of type identified by class reference in constant pool index (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек → objectref
     */
    NEW(187), // visitTypeInsn

    /**
     * create new array with count elements of primitive type identified by atype
     * <p>Параметры 1: atype
     * <p>Стек count → arrayref
     */
    NEWARRAY(188), // visitIntInsn

    /**
     * create a new array of references of length count and component type identified by the class reference index (indexbyte1 &lt;&lt; 8 | indexbyte2) in the constant pool
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек count → arrayref
     */
    ANEWARRAY(189), // visitTypeInsn

    /**
     * get the length of an array
     * <p>Стек arrayref → length
     */
    ARRAYLENGTH(190), // visitInsn

    /**
     * throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)
     * <p>Стек objectref → [empty], objectref
     */
    ATHROW(191), 

    /**
     * checks whether an objectref is of a certain type, the class reference of which is in the constant pool at index (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек objectref → objectref
     */
    CHECKCAST(192), // visitTypeInsn

    /**
     * determines if an object objectref is of a given type, identified by class reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
     * <p>Параметры 2: indexbyte1, indexbyte2
     * <p>Стек objectref → result
     */
    INSTANCEOF(193), 

    /**
     * enter monitor for object ("grab the lock" – start of synchronized() section)
     * <p>Стек objectref → 
     */
    MONITORENTER(194), // visitInsn

    /**
     * exit monitor for object ("release the lock" – end of synchronized() section)
     * <p>Стек objectref → 
     */
    MONITOREXIT(195), 

    /**
     * create a new array of dimensions dimensions of type identified by class reference in constant pool index 
     * (indexbyte1 &lt;&lt; 8 | indexbyte2); the sizes of each dimension is identified by count1, [count2, etc.]
     * <p>Параметры 3: indexbyte1, indexbyte2, dimensions
     * <p>Стек count1, [count2,...] → arrayref
     * <p><a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.multianewarray">Из оф доки</a>
     * Format
     * 
     * <pre>
     * multianewarray
     * indexbyte1
     * indexbyte2
     * dimensions
     * </pre>
     * 
     * Forms
     * <pre>
     * multianewarray = 197 (0xc5)</pre>
     * 
     * Operand Stack
     * <pre>
     * .., count1, [count2, ...] →
     * .., arrayref
     * </pre>
     * 
     * Description
     * 
     * <p>
     * The dimensions operand is an unsigned byte that must be greater than or equal to 1. It represents the number of dimensions of the array to be created. The operand stack must contain dimensions values. Each such value represents the number of components in a dimension of the array to be created, must be of type int, and must be non-negative. The count1 is the desired length in the first dimension, count2 in the second, etc.
     * 
     * <p>
     * All of the count values are popped off the operand stack. The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at the index must be a symbolic reference to a class, array, or interface type. The named class, array, or interface type is resolved (§5.4.3.1). The resulting entry must be an array class type of dimensionality greater than or equal to dimensions.
     * 
     * <p>
     * A new multidimensional array of the array type is allocated from the garbage-collected heap. If any count value is zero, no subsequent dimensions are allocated. The components of the array in the first dimension are initialized to subarrays of the type of the second dimension, and so on. The components of the last allocated dimension of the array are initialized to the default initial value (§2.3, §2.4) for the element type of the array type. A reference arrayref to the new array is pushed onto the operand stack.
     * 
     * <p>
     * Linking Exceptions
     * 
     * <p>During resolution of the symbolic reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be thrown.
     * <p>Otherwise, if the current class does not have permission to access the element type of the resolved array class, multianewarray throws an IllegalAccessError.
     * 
     * 
     * <p>Run-time Exception
     * 
     * <p>Otherwise, if any of the dimensions values on the operand stack are less than zero, the multianewarray instruction throws a NegativeArraySizeException.
     * 
     * <p>Notes
     * 
     * <p>It may be more efficient to use newarray or anewarray (§newarray, §anewarray) when creating an array of a single dimension.
     * <p>The array class referenced via the run-time constant pool may have more dimensions than the dimensions operand of the multianewarray instruction. In that case, only the first dimensions of the dimensions of the array are created.
     */
    MULTIANEWARRAY(197), // visitMultiANewArrayInsn

    /**
     * if value is null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     * <p><a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.ifnull">Из оф доки</a>
     * Description
     * <p>The value must of type reference. It is popped from the operand stack. If value is null, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, where the offset is calculated to be (branchbyte1 &lt;&lt; 8) | branchbyte2. Execution then proceeds at that offset from the address of the opcode of this ifnull instruction. The target address must be that of an opcode of an instruction within the method that contains this ifnull instruction.
     * <p>Otherwise, execution proceeds at the address of the instruction following this ifnull instruction.
     */
    IFNULL(198), // visitJumpInsn

    /**
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.ifnonnull">Официальная дока</a>
     * if value is not null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
     * <p>Параметры 2: branchbyte1, branchbyte2
     * <p>Стек value →
     * <p>Description
     * <p>The value must be of type reference. It is popped from the operand stack. If value is not null, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, where the offset is calculated to be (branchbyte1 &lt;&lt; 8) | branchbyte2. Execution then proceeds at that offset from the address of the opcode of this ifnonnull instruction. The target address must be that of an opcode of an instruction within the method that contains this ifnonnull instruction.
     * <p>Otherwise, execution proceeds at the address of the instruction following this ifnonnull instruction.
     */
    IFNONNULL(199), 
    ;
    public final int code;
    OpCode(int code){
        this.code = code;
    }
    public static Optional<OpCode> code(int c){
        for( var ic : values() ){
            if( ic.code==c )return Optional.of(ic);
        }
        return Optional.empty();
    }
}
