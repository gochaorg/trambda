package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

/**
 * the opcode of the local variable instruction to be visited. This opcode is either
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.iload">ILOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.lload">LLOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.fload">FLOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.dload">DLOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.aload">ALOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.istore">ISTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.lstore">LSTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.fstore">FSTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.dstore">DSTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.astore">ASTORE</a> or
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.ret">RET</a>.
 *
 * <hr>
 *
 * <h1>iload</h1>
 *
 * <h2 style="font-weight: bold">Operation</h2>
 * Load int from local variable
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * iload
 * index
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * iload = 21 (0x15)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 * ... → <br>
 * ..., value <br>
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the
 * current frame (§2.6). The local variable at index must contain an int.
 * The value of the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The iload opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 *
 * lload
 * <h2 style="font-weight: bold">Operation</h2>
 * Load long from local variable
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * lload
 * index
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * lload = 22 (0x16)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array
 * of the current frame (§2.6). The local variable at index must contain a long.
 * The value of the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The lload opcode can be used in conjunction with the wide instruction (§wide) to
 * access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * fload
 * <h2 style="font-weight: bold">Operation</h2>
 * Load float from local variable
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * fload
 * index
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * fload = 23 (0x17)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The local variable at index must contain a float. The value of the local variable at index is pushed onto
 * the operand stack.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The fload opcode can be used in conjunction with the wide instruction (§wide) to access a local
 * variable using a two-byte unsigned index.
 * <hr>
 *
 * fload_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 * Load float from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * fload_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * fload_0 = 34 (0x22)
 * fload_1 = 35 (0x23)
 * fload_2 = 36 (0x24)
 * fload_3 = 37 (0x25)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The local variable at &lt;n&gt; must contain a float.
 * The value of the local variable at &lt;n&gt; is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * Each of the fload_&lt;n&gt; instructions is the same as fload with an index of &lt;n>,
 * except that the operand &lt;n> is implicit.
 *
 * <hr>
 * dload
 * <h2 style="font-weight:bold">Operation</h2>
 * Load double from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * dload
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 * dload = 24 (0x18)
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array
 * of the current frame (§2.6). The local variable at index must contain a double.
 * The value of the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * The dload opcode can be used in conjunction with the wide instruction (§wide) to access a local
 * variable using a two-byte unsigned index.
 *
 * <hr>
 * dload_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 * Load double from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * dload_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 * <pre>
 * dload_0 = 38 (0x26)
 * dload_1 = 39 (0x27)
 * dload_2 = 40 (0x28)
 * dload_3 = 41 (0x29)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * Both &lt;n&gt; and &lt;n&gt;+1 must be indices into the local variable array of the current frame (§2.6).
 * The local variable at &lt;n&gt; must contain a double. The value of the local variable at &lt;n&gt;
 * is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * Each of the dload_&lt;n&gt; instructions is the same as dload with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * aload
 * <h2 style="font-weight:bold">Operation</h2>
 * Load reference from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * aload
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 * aload = 25 (0x19)
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., objectref
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The local variable at index must contain a reference.
 * The objectref in the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * The aload instruction cannot be used to load a value of type returnAddress from a
 * local variable onto the operand stack. This asymmetry with the astore instruction (§astore) is intentional.
 *
 * <p>
 * The aload opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * aload_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 * Load reference from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * aload_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * aload_0 = 42 (0x2a)
 * aload_1 = 43 (0x2b)
 * aload_2 = 44 (0x2c)
 * aload_3 = 45 (0x2d)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., objectref
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The local variable at &lt;n&gt; must contain a reference.
 * The objectref in the local variable at &lt;n&gt; is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * An aload_&lt;n&gt; instruction cannot be used to load a value of type returnAddress
 * from a local variable onto the operand stack.
 * This asymmetry with the corresponding astore_&lt;n&gt; instruction (§astore_&lt;n&gt;) is intentional.
 *
 * <p>
 * Each of the aload_&lt;n&gt; instructions is
 * the same as aload with an index of &lt;n&gt;, except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * istore
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store int into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * istore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * istore = 54 (0x36)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte that must be an index into the local variable array of the current
 * frame (§2.6). The value on the top of the operand stack must be of type int.
 * It is popped from the operand stack, and the value of the local variable at index is set to value.
 * Notes
 *
 * <p>
 * The istore opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * istore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store int into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * istore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * istore_0 = 59 (0x3b)
 * istore_1 = 60 (0x3c)
 * istore_2 = 61 (0x3d)
 * istore_3 = 62 (0x3e)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type int.
 * It is popped from the operand stack, and the value of the local variable at &lt;n&gt; is set to value.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the istore_&lt;n&gt; instructions is the same as istore with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * lstore
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store long into local variable
 *<h2 style="font-weight:bold"> Format</h2>
 *
 * <pre>
 * lstore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * lstore = 55 (0x37)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type long.
 * It is popped from the operand stack, and the local variables at index and index+1 are set to value.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The lstore opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * lstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store long into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * lstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * lstore_0 = 63 (0x3f)
 * lstore_1 = 64 (0x40)
 * lstore_2 = 65 (0x41)
 * lstore_3 = 66 (0x42)
 * </pre>
 * 
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 * 
 * <h2 style="font-weight:bold">Description</h2>
 *
 * Both &lt;n&gt; and &lt;n&gt;+1 must be indices into the local variable array of the 
 * current frame (§2.6). The value on the top of the operand stack must be of type long. 
 * It is popped from the operand stack, and the local variables at &lt;n&gt; and &lt;n&gt;+1 are set to value.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the lstore_&lt;n&gt; instructions is the same as lstore with an index of &lt;n&gt;, except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * fstore
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store float into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * fstore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * fstore = 56 (0x38)
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type float.
 * It is popped from the operand stack and undergoes value set conversion (§2.8.3),
 * resulting in value'. The value of the local variable at index is set to value'.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The fstore opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * fstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store float into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * fstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * fstore_0 = 67 (0x43)
 * fstore_1 = 68 (0x44)
 * fstore_2 = 69 (0x45)
 * fstore_3 = 70 (0x46)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type float.
 * It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'.
 * The value of the local variable at &lt;n&gt; is set to value'.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the fstore_&lt;n&gt; instructions is the same as fstore
 * with an index of &lt;n&gt;, except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * dstore
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store double into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * dstore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * dstore = 57 (0x39)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current
 * frame (§2.6). The value on the top of the operand stack must be of type double. It is popped from the operand
 * stack and undergoes value set conversion (§2.8.3), resulting in value'.
 * The local variables at index and index+1 are set to value'.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The dstore opcode can be used in conjunction with the wide instruction (§wide) to access a local
 * variable using a two-byte unsigned index.
 *
 * <hr>
 * dstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store double into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * dstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * dstore_0 = 71 (0x47)
 * dstore_1 = 72 (0x48)
 * dstore_2 = 73 (0x49)
 * dstore_3 = 74 (0x4a)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * Both &lt;n&gt; and &lt;n&gt;+1 must be indices into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type double. It is popped from the operand
 * stack and undergoes value set conversion (§2.8.3), resulting in value'.
 * The local variables at &lt;n&gt; and &lt;n&gt;+1 are set to value'.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the dstore_&lt;n&gt; instructions is the same as dstore with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * astore
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store reference into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * astore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * astore = 58 (0x3a)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., objectref →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The objectref on the top of the operand stack must be of type returnAddress or of type reference.
 * It is popped from the operand stack, and the value of the local variable at index is set to objectref.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The astore instruction is used with an objectref of type returnAddress when implementing the finally
 * clause of the Java programming language (§3.13).
 *
 * <p>
 * The aload instruction (§aload) cannot be used to load a value of type returnAddress from a
 * local variable onto the operand stack. This asymmetry with the astore instruction is intentional.
 *
 * <p>
 * The astore opcode can be used in conjunction with the wide instruction (§wide) to access a
 * local variable using a two-byte unsigned index.
 *
 * <hr>
 * astore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store reference into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * astore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * astore_0 = 75 (0x4b)
 * astore_1 = 76 (0x4c)
 * astore_2 = 77 (0x4d)
 * astore_3 = 78 (0x4e)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., objectref →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The objectref on the top of the operand stack must be of type returnAddress or of type reference.
 * It is popped from the operand stack, and the value of the local variable at &lt;n&gt; is set to objectref.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * An astore_&lt;n&gt; instruction is used with an objectref of type returnAddress when implementing
 * the finally clauses of the Java programming language (§3.13).
 *
 * <p>
 * An aload_&lt;n&gt; instruction (§aload_&lt;n&gt;) cannot be used to load a value of type
 * returnAddress from a local variable onto the operand stack. This asymmetry with the
 * corresponding astore_&lt;n&gt; instruction is intentional.
 *
 * <p>
 * Each of the astore_&lt;n&gt; instructions is the same as astore with an index of &lt;n&gt;, except
 * that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * ret
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Return from subroutine
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * ret
 * index
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * ret = 169 (0xa9)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * No change
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte between 0 and 255, inclusive.
 * The local variable at index in the current frame (§2.6) must contain a value of type returnAddress.
 * The contents of the local variable are written into
 * the Java Virtual Machine's pc register, and execution continues there.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Note that jsr (§jsr) pushes the address onto the operand stack and ret gets it out of a local variable.
 * This asymmetry is intentional.
 *
 * <p>
 * In Oracle's implementation of a compiler for the Java programming language prior to Java SE 6,
 * the ret instruction was used with the jsr and jsr_w instructions (§jsr, §jsr_w)
 * in the implementation of the finally clause (§3.13, §4.10.2.5).
 *
 * <p>
 * The ret instruction should not be confused with the return instruction (§return).
 * A return instruction returns control from a method to its invoker,
 * without passing any value back to the invoker.
 *
 * <p>
 * The ret opcode can be used in conjunction with the wide instruction (§wide) to
 * access a local variable using a two-byte unsigned index.
 */
public class MVarInsn extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MVarInsn(){}
    public MVarInsn(int op, int vi){
        opcode = op;
        variable = vi;
    }
    public MVarInsn(MVarInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        opcode = sample.opcode;
        variable = sample.variable;
    }

    //region opcode
    private int opcode;
    public int getOpcode(){
        return opcode;
    }
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region variable
    private int variable;
    public int getVariable(){
        return variable;
    }
    public void setVariable(int variable){
        this.variable = variable;
    }
    //endregion

    public String toString(){
        return MVarInsn.class.getSimpleName()+" "+ OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+" "+variable;
    }
}
