package xyz.cofe.trambda;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.bm.LdcType;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.mth.*;

/**
 * Создает дамп байт кода метода, используется в {@link AsmQuery}.
 *
 * <p>
 *
 * A visitor to visit a Java method.
 * The methods of this class must be called in the following order:
 *
 * <pre>
 * ( visitParameter )*
 * [ visitAnnotationDefault ]
 * ( visitAnnotation
 * | visitAnnotableParameterCount
 * | visitParameterAnnotation visitTypeAnnotation
 * | visitAttribute
 * )*
 * [ visitCode
 *     ( visitFrame
 *     | visit&lt;i&gt;X&lt;/i&gt;Insn
 *     | visitLabel
 *     | visitInsnAnnotation
 *     | visitTryCatchBlock
 *     | visitTryCatchAnnotation
 *     | visitLocalVariable
 *     | visitLocalVariableAnnotation
 *     | visitLineNumber
 *     )* visitMaxs
 * ] visitEnd.
 * </pre>
 *
 * <p>
 * In addition, the <b>visit&lt;i&gt;X&lt;/i&gt;Insn</b> and visitLabel methods must be called in the sequential
 * order of the bytecode instructions of the visited code,
 *
 * <p>
 * <b>visitInsnAnnotation</b> must be called after the annotated instruction,
 *
 * <p>
 * <b>visitTryCatchBlock</b> must be called before the labels passed as arguments have been visited,
 * <b>visitTryCatchBlockAnnotation</b> must be called after the corresponding try catch block has been visited,
 *
 * <p>
 * and the <b>visitLocalVariable</b>, <b>visitLocalVariableAnnotation</b> and <b>visitLineNumber</b> methods must be called after
 * the labels passed as arguments have been visited.
 */
public class MethodDump extends MethodVisitor implements Opcodes {
    private void dump(String message,Object...args){
        if( message==null )return;
        if( args==null || args.length==0 ){
            System.out.println(message);
        }else{
            System.out.print(message);
            for( var a : args ){
                System.out.print(" ");
                System.out.print(a);
            }
            System.out.println();
        }
    }

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public MethodDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    protected void emit(ByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
    }

    /**
     * Constructs a new {@link MethodVisitor}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one of {@link
     *            Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     */
    public MethodDump(int api){
        super(api);
    }

    /**
     * Constructs a new {@link MethodVisitor}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this visitor must delegate method calls. May
     */
    public MethodDump(int api, MethodVisitor methodVisitor){
        super(api, methodVisitor);
    }

    /**
     * Visits a parameter of this method.
     *
     * @param name   parameter name or {@literal null} if none is provided.
     * @param access the parameter's access flags, only {@code ACC_FINAL}, {@code ACC_SYNTHETIC}
     *               or/and {@code ACC_MANDATED} are allowed (see {@link Opcodes}).
     */
    @Override
    public void visitParameter(String name, int access){
        emit(new MParameter(name,access));
    }

    /**
     * Visits the default value of this annotation interface method.
     *
     * @return a visitor to the visit the actual default value of this annotation interface method, or
     * {@literal null} if this visitor is not interested in visiting this default value. The
     * 'name' parameters passed to the methods of this annotation visitor are ignored. Moreover,
     * exacly one visit method must be called on this annotation visitor, followed by visitEnd.
     */
    @Override
    public AnnotationVisitor visitAnnotationDefault(){
        MAnnotationDefault bc = new MAnnotationDefault();
        AnnotationDump dump = new AnnotationDump(this.api);

        dump.byteCode( this.byteCodeConsumer,bc );

        emit(bc);
        return dump;
    }

    /**
     * Visits an annotation of this method.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
        MAnnotation ann = new MAnnotation(descriptor,visible);
        AnnotationDump dump = new AnnotationDump(this.api);

        dump.byteCode( this.byteCodeConsumer,ann );

        emit(ann);
        return dump;
    }

    /**
     * Visits an annotation on a type in the method signature.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link org.objectweb.asm.TypeReference#METHOD_TYPE_PARAMETER}, {@link
     *                   org.objectweb.asm.TypeReference#METHOD_TYPE_PARAMETER_BOUND}, {@link org.objectweb.asm.TypeReference#METHOD_RETURN}, {@link
     *                   org.objectweb.asm.TypeReference#METHOD_RECEIVER}, {@link org.objectweb.asm.TypeReference#METHOD_FORMAL_PARAMETER} or {@link
     *                   org.objectweb.asm.TypeReference#THROWS}. See {@link org.objectweb.asm.TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        MTypeAnnotation ta = new MTypeAnnotation();

        AnnotationDump dump = new AnnotationDump(this.api);
        dump.byteCode( this.byteCodeConsumer,ta );

        ta.setTypeRef(typeRef);
        ta.setTypePath(typePath!=null ? typePath.toString() : null);
        ta.setDescriptor(descriptor);
        ta.setVisible(visible);

        emit(ta);
        return dump;
    }

    /**
     * Visits the number of method parameters that can have annotations. By default (i.e. when this
     * method is not called), all the method parameters defined by the method descriptor can have
     * annotations.
     *
     * @param parameterCount the number of method parameters than can have annotations. This number
     *                       must be less or equal than the number of parameter types in the method descriptor. It can
     *                       be strictly less when a method has synthetic parameters and when these parameters are
     *                       ignored when computing parameter indices for the purpose of parameter annotations (see
     *                       https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
     * @param visible        {@literal true} to define the number of method parameters that can have
     *                       annotations visible at runtime, {@literal false} to define the number of method parameters
     */
    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible){
        emit( new MAnnotableParameterCount(parameterCount,visible));
    }

    /**
     * Visits an annotation of a parameter this method.
     *
     * @param parameter  the parameter index. This index must be strictly smaller than the number of
     *                   parameters in the method descriptor, and strictly smaller than the parameter count
     *                   specified in {@link #visitAnnotableParameterCount}. Important note: <i>a parameter index i
     *                   is not required to correspond to the i'th parameter descriptor in the method
     *                   descriptor</i>, in particular in case of synthetic parameters (see
     *                   https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible){
        MParameterAnnotation pa = new MParameterAnnotation();

        AnnotationDump dump = new AnnotationDump(this.api);
        dump.byteCode( this.byteCodeConsumer,pa );

        pa.setParameter(parameter);
        pa.setDescriptor(descriptor);
        pa.setVisible(visible);

        emit(pa);
        return dump;
    }

    /**
     * Visits a non standard attribute of this method.
     *
     * @param attribute an attribute.
     */
    @Override
    public void visitAttribute(Attribute attribute){
        dump("Attribute",attribute);
        super.visitAttribute(attribute);
    }

    /**
     * Starts the visit of the method's code, if any (i.e. non abstract method).
     */
    @Override
    public void visitCode(){
        emit(new MCode());
    }

    /**
     * Visits the current state of the local variables and operand stack elements. This method must(*)
     * be called <i>just before</i> any instruction <b>i</b> that follows an unconditional branch
     * instruction such as GOTO or THROW, that is the target of a jump instruction, or that starts an
     * exception handler block. The visited types must describe the values of the local variables and
     * of the operand stack elements <i>just before</i> <b>i</b> is executed.<br>
     * <br>
     * (*) this is mandatory only for classes whose version is greater than or equal to {@link
     * Opcodes#V1_6}. <br>
     * <br>
     * The frames of a method must be given either in expanded form, or in compressed form (all frames
     * must use the same format, i.e. you must not mix expanded and compressed frames within a single
     * method):
     *
     * <ul>
     *   <li>In expanded form, all frames must have the F_NEW type.
     *   <li>In compressed form, frames are basically "deltas" from the state of the previous frame:
     *       <ul>
     *         <li>{@link Opcodes#F_SAME} representing frame with exactly the same locals as the
     *             previous frame and with the empty stack.
     *         <li>{@link Opcodes#F_SAME1} representing frame with exactly the same locals as the
     *             previous frame and with single value on the stack ( <code>numStack</code> is 1 and
     *             <code>stack[0]</code> contains value for the type of the stack item).
     *         <li>{@link Opcodes#F_APPEND} representing frame with current locals are the same as the
     *             locals in the previous frame, except that additional locals are defined (<code>
     *             numLocal</code> is 1, 2 or 3 and <code>local</code> elements contains values
     *             representing added types).
     *         <li>{@link Opcodes#F_CHOP} representing frame with current locals are the same as the
     *             locals in the previous frame, except that the last 1-3 locals are absent and with
     *             the empty stack (<code>numLocal</code> is 1, 2 or 3).
     *         <li>{@link Opcodes#F_FULL} representing complete frame data.
     *       </ul>
     * </ul>
     *
     * <br>
     * In both cases the first frame, corresponding to the method's parameters and access flags, is
     * implicit and must not be visited. Also, it is illegal to visit two or more frames for the same
     * code location (i.e., at least one instruction must be visited between two calls to visitFrame).
     *
     * @param type     the type of this stack map frame. Must be {@link Opcodes#F_NEW} for expanded
     *                 frames, or {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link
     *                 Opcodes#F_SAME} or {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
     * @param numLocal the number of local variables in the visited frame.
     * @param local    the local variable types in this frame. This array must not be modified. Primitive
     *                 types are represented by {@link Opcodes#TOP}, {@link Opcodes#INTEGER}, {@link
     *                 Opcodes#FLOAT}, {@link Opcodes#LONG}, {@link Opcodes#DOUBLE}, {@link Opcodes#NULL} or
     *                 {@link Opcodes#UNINITIALIZED_THIS} (long and double are represented by a single element).
     *                 Reference types are represented by String objects (representing internal names), and
     *                 uninitialized types by Label objects (this label designates the NEW instruction that
     *                 created this uninitialized value).
     * @param numStack the number of operand stack elements in the visited frame.
     * @param stack    the operand stack types in this frame. This array must not be modified. Its
     *                 content has the same format as the "local" array.
     * @throws IllegalStateException if a frame is visited just after another one, without any
     *                               instruction between the two (unless this frame is a Opcodes#F_SAME frame, in which case it
     *                               is silently ignored).
     */
    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack){
        emit(new MFrame(type,numLocal,local,numStack,stack));
    }

    /**
     * Visits a zero operand instruction.
     *
     * @param opcode the opcode of the instruction to be visited. This opcode is either NOP,
     *               ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
     *               LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD,
     *               FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE,
     *               AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2,
     *               SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV,
     *               FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR,
     *               LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I,
     *               D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
     *               DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
     */
    @Override
    public void visitInsn(int opcode){
        emit(new MInsn(opcode));
    }

    /**
     * Visits an instruction with a single int operand.
     *
     * @param opcode  the opcode of the instruction to be visited. This opcode is either BIPUSH, SIPUSH
     *                or NEWARRAY.
     * @param operand the operand of the instruction to be visited.<br>
     *                When opcode is BIPUSH, operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.
     *                <br>
     *                When opcode is SIPUSH, operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.
     *                <br>
     *                When opcode is NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link
     *                Opcodes#T_CHAR}, {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE},
     *                {@link Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
     */
    @Override
    public void visitIntInsn(int opcode, int operand){
        emit(new MIntInsn(opcode,operand));
    }

    /**
     * Visits a local variable instruction. A local variable instruction is an instruction that loads
     * or stores the value of a local variable.
     *
     * @param opcode the opcode of the local variable instruction to be visited. This opcode is either
     *               ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var    the operand of the instruction to be visited. This operand is the index of a local
     */
    @Override
    public void visitVarInsn(int opcode, int var){
        emit(new MVarInsn(opcode,var));
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that takes the internal name of
     * a class as parameter.
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either NEW,
     *               ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param type   the operand of the instruction to be visited. This operand must be the internal
     *               name of an object or array class (see {@link org.objectweb.asm.Type#getInternalName()}).
     */
    @Override
    public void visitTypeInsn(int opcode, String type){
        emit(new MTypeInsn(opcode,type));
    }

    /**
     * Visits a field instruction. A field instruction is an instruction that loads or stores the
     * value of a field of an object.
     *
     * @param opcode     the opcode of the type instruction to be visited. This opcode is either
     *                   GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner      the internal name of the field's owner class (see {@link org.objectweb.asm.Type#getInternalName()}).
     * @param name       the field's name.
     * @param descriptor the field's descriptor (see {@link org.objectweb.asm.Type}).
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor){
        emit(new MFieldInsn(opcode,owner,name,descriptor));
    }

//    /**
//     * Visits a method instruction. A method instruction is an instruction that invokes a method.
//     *
//     * @param opcode     the opcode of the type instruction to be visited. This opcode is either
//     *                   INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
//     * @param owner      the internal name of the method's owner class (see {@link
//     *                   org.objectweb.asm.Type#getInternalName()}).
//     * @param name       the method's name.
//     * @param descriptor the method's descriptor (see {@link org.objectweb.asm.Type}).
//     * @deprecated use {@link #visitMethodInsn(int, String, String, String, boolean)} instead.
//     */
//    @Override
//    public void visitMethodInsn(int opcode, String owner, String name, String descriptor){
//        super.visitMethodInsn(opcode, owner, name, descriptor);
//    }

    /**
     * Visits a method instruction. A method instruction is an instruction that invokes a method.
     *
     * @param opcode      the opcode of the type instruction to be visited. This opcode is either
     *                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     * @param owner       the internal name of the method's owner class (see {@link
     *                    org.objectweb.asm.Type#getInternalName()}).
     * @param name        the method's name.
     * @param descriptor  the method's descriptor (see {@link org.objectweb.asm.Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface){
        emit(new MMethodInsn(opcode,owner,name,descriptor,isInterface));
    }

    /**
     * Visits an invokedynamic instruction.
     *
     * @param name                     the method's name.
     * @param descriptor               the method's descriptor (see {@link org.objectweb.asm.Type}).
     * @param bootstrapMethodHandle    the bootstrap method.
     * @param bootstrapMethodArguments the bootstrap method constant arguments. Each argument must be
     *                                 an {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link
     *                                 org.objectweb.asm.Type}, {@link Handle} or {@link org.objectweb.asm.ConstantDynamic} value. This method is allowed to modify
     */
    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments){
        emit(new MInvokeDynamicInsn(name,descriptor,bootstrapMethodHandle,bootstrapMethodArguments));
    }

    /**
     * Visits a jump instruction. A jump instruction is an instruction that may jump to another
     * instruction.
     *
     * @param opcode the opcode of the type instruction to be visited. This opcode is either IFEQ,
     *               IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT,
     *               IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
     * @param label  the operand of the instruction to be visited. This operand is a label that
     */
    @Override
    public void visitJumpInsn(int opcode, Label label){
        emit(new MJumpInsn(opcode, label!=null ? label.toString():null));
    }

    /**
     * Visits a label. A label designates the instruction that will be visited just after it.
     *
     * @param label a {@link Label} object.
     */
    @Override
    public void visitLabel(Label label){
        emit(new MLabel(label.toString()));
    }

    /**
     * Visits a LDC instruction. Note that new constant types may be added in future versions of the
     * Java Virtual Machine. To easily detect new constant types, implementations of this method
     * should check for unexpected constant types, like this:
     *
     * <pre>
     * if (cst instanceof Integer) {
     *     // ...
     * } else if (cst instanceof Float) {
     *     // ...
     * } else if (cst instanceof Long) {
     *     // ...
     * } else if (cst instanceof Double) {
     *     // ...
     * } else if (cst instanceof String) {
     *     // ...
     * } else if (cst instanceof org.objectweb.asm.Type) {
     *     int sort = ((org.objectweb.asm.Type) cst).getSort();
     *     if (sort == org.objectweb.asm.Type.OBJECT) {
     *         // ...
     *     } else if (sort == org.objectweb.asm.Type.ARRAY) {
     *         // ...
     *     } else if (sort == org.objectweb.asm.Type.METHOD) {
     *         // ...
     *     } else {
     *         // throw an exception
     *     }
     * } else if (cst instanceof Handle) {
     *     // ...
     * } else if (cst instanceof ConstantDynamic) {
     *     // ...
     * } else {
     *     // throw an exception
     * }
     * </pre>
     *
     * @param value the constant to be loaded on the stack. This parameter must be a non null {@link
     *              Integer}, a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link
     *              org.objectweb.asm.Type} of OBJECT or ARRAY sort for {@code .class} constants, for classes whose version is
     *              49, a {@link org.objectweb.asm.Type} of METHOD sort for MethodType, a {@link Handle} for MethodHandle
     *              constants, for classes whose version is 51 or a {@link org.objectweb.asm.ConstantDynamic} for a constant
     *              dynamic for classes whose version is 55.
     */
    @Override
    public void visitLdcInsn(Object value){
        if ( value instanceof Integer) {
            emit(new MLdcInsn(value, LdcType.Integer));
        } else if ( value instanceof Float) {
            emit(new MLdcInsn(value,LdcType.Float));
        } else if ( value instanceof Long) {
            emit(new MLdcInsn(value,LdcType.Long));
        } else if ( value instanceof Double) {
            emit(new MLdcInsn(value,LdcType.Double));
        } else if ( value instanceof String) {
            emit(new MLdcInsn(value,LdcType.String));
        } else if ( value instanceof org.objectweb.asm.Type) {
            int sort = ((org.objectweb.asm.Type) value).getSort();
            if (sort == org.objectweb.asm.Type.OBJECT) {
                //emit(new LdcInsn(cst,LdcType.Object));
                throw new IllegalArgumentException("not impl ldc object");
            } else if (sort == org.objectweb.asm.Type.ARRAY) {
                // ...
                //emit(new LdcInsn(cst,LdcType.Object));
                throw new IllegalArgumentException("not impl ldc array");
            } else if (sort == org.objectweb.asm.Type.METHOD) {
                // ...
                //emit(new LdcInsn(cst,LdcType.Object));
                throw new IllegalArgumentException("not impl ldc method");
            } else {
                throw new UnsupportedOperationException("unsupported ldc sort="+sort);
            }
        } else if ( value instanceof Handle) {
            // ...
            var hdl = (Handle) value;
            var hdl0 = new MHandle(hdl);
            emit(new MLdcInsn(hdl0,LdcType.Handle));
        } else if ( value instanceof ConstantDynamic ) {
            // ...
            throw new UnsupportedOperationException("not impl ldc ConstantDynamic");
        } else {
            throw new UnsupportedOperationException("unsupported ldc of "+ value);
        }
        //super.visitLdcInsn(value);
    }

    /**
     * Visits an IINC instruction.
     *
     * @param var       index of the local variable to be incremented.
     * @param increment amount to increment the local variable by.
     */
    @Override
    public void visitIincInsn(int var, int increment){
        emit(new MIincInsn(var,increment));
    }

    /**
     * Visits a TABLESWITCH instruction.
     *
     * @param min    the minimum key value.
     * @param max    the maximum key value.
     * @param dflt   beginning of the default handler block.
     * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
     *               handler block for the {@code min + i} key.
     */
    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels){
        String defLbl = dflt!=null ? dflt.toString() : null;
        String[] lbls = labels!=null ?
            List.of(labels).stream().map(l -> l != null ? l.toString() : null).toArray(String[]::new) : null;
        emit(new MTableSwitchInsn(min,max,defLbl,lbls));
    }

    /**
     * Visits a LOOKUPSWITCH instruction.
     *
     * @param dflt   beginning of the default handler block.
     * @param keys   the values of the keys.
     * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
     *               handler block for the {@code keys[i]} key.
     */
    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels){
        String defLbl = dflt!=null ? dflt.toString() : null;
        String[] lbls = labels!=null ?
            List.of(labels).stream().map(l -> l != null ? l.toString() : null).toArray(String[]::new) : null;
        emit(new MLookupSwitchInsn(defLbl, keys, lbls));
    }

    /**
     * Visits a MULTIANEWARRAY instruction.
     *
     * @param descriptor    an array type descriptor (see {@link org.objectweb.asm.Type}).
     * @param numDimensions the number of dimensions of the array to allocate.
     */
    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions){
        emit(new MMultiANewArrayInsn(descriptor,numDimensions));
    }

    /**
     * Visits an annotation on an instruction. This method must be called just <i>after</i> the
     * annotated instruction. It can be called several times for the same instruction.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link org.objectweb.asm.TypeReference#INSTANCEOF}, {@link org.objectweb.asm.TypeReference#NEW}, {@link
     *                   org.objectweb.asm.TypeReference#CONSTRUCTOR_REFERENCE}, {@link org.objectweb.asm.TypeReference#METHOD_REFERENCE}, {@link
     *                   org.objectweb.asm.TypeReference#CAST}, {@link org.objectweb.asm.TypeReference#CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT}, {@link
     *                   org.objectweb.asm.TypeReference#METHOD_INVOCATION_TYPE_ARGUMENT}, {@link
     *                   org.objectweb.asm.TypeReference#CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or {@link
     *                   org.objectweb.asm.TypeReference#METHOD_REFERENCE_TYPE_ARGUMENT}. See {@link org.objectweb.asm.TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        MInsnAnnotation ia = new MInsnAnnotation();
        AnnotationDump dump = new AnnotationDump(this.api);

        dump.byteCode( this.byteCodeConsumer,ia );

        ia.setTypeRef(typeRef);
        ia.setTypePath(typePath!=null ? typePath.toString() : null);
        ia.setDescriptor(descriptor);
        ia.setVisible(visible);

        emit(ia);

        return dump;
    }

    /**
     * Visits a try catch block.
     *
     * @param start   the beginning of the exception handler's scope (inclusive).
     * @param end     the end of the exception handler's scope (exclusive).
     * @param handler the beginning of the exception handler's code.
     * @param type    the internal name of the type of exceptions handled by the handler, or {@literal
     *                null} to catch any exceptions (for "finally" blocks).
     * @throws IllegalArgumentException if one of the labels has already been visited by this visitor
     *                                  (by the {@link #visitLabel} method).
     */
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type){
        emit(new MTryCatchBlock(
            start!=null ? start.toString() : null,
            end!=null ? end.toString() : null,
            handler!=null ? handler.toString() : null,
            type
        ));
    }

    /**
     * Visits an annotation on an exception handler type. This method must be called <i>after</i> the
     * {@link #visitTryCatchBlock} for the annotated exception handler. It can be called several times
     * for the same exception handler.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link org.objectweb.asm.TypeReference#EXCEPTION_PARAMETER}. See {@link org.objectweb.asm.TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        MTryCatchAnnotation a = new MTryCatchAnnotation(typeRef,typePath!=null ? typePath.toString() : null,descriptor,visible);

        AnnotationDump dump = new AnnotationDump(this.api);
        dump.byteCode( this.byteCodeConsumer,a );

        emit(a);
        return dump;
    }

    /**
     * Visits a local variable declaration.
     *
     * @param name       the name of a local variable.
     * @param descriptor the type descriptor of this local variable.
     * @param signature  the type signature of this local variable. May be {@literal null} if the local
     *                   variable type does not use generic types.
     * @param start      the first instruction corresponding to the scope of this local variable
     *                   (inclusive).
     * @param end        the last instruction corresponding to the scope of this local variable (exclusive).
     * @param index      the local variable's index.
     * @throws IllegalArgumentException if one of the labels has not already been visited by this
     *                                  visitor (by the {@link #visitLabel} method).
     */
    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index){
        emit(new MLocalVariable(name,descriptor,signature,start!=null ? start.toString() : null, end!=null ? end.toString() : null, index));
    }

    /**
     * Visits an annotation on a local variable type.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link org.objectweb.asm.TypeReference#LOCAL_VARIABLE} or {@link org.objectweb.asm.TypeReference#RESOURCE_VARIABLE}. See {@link
     *                   org.objectweb.asm.TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *                   'typeRef' as a whole.
     * @param start      the fist instructions corresponding to the continuous ranges that make the scope
     *                   of this local variable (inclusive).
     * @param end        the last instructions corresponding to the continuous ranges that make the scope of
     *                   this local variable (exclusive). This array must have the same size as the 'start' array.
     * @param index      the local variable's index in each range. This array must have the same size as
     *                   the 'start' array.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     * interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible){
        MLocalVariableAnnotation a = new MLocalVariableAnnotation();

        AnnotationDump dump = new AnnotationDump(this.api);
        dump.byteCode( this.byteCodeConsumer,a );

        a.setTypeRef(typeRef);
        a.setTypePath(typePath!=null ? typePath.toString() : null);
        if( start!=null ){
            a.setStartLabels(Arrays.stream(start).map(s -> s!=null ? s.toString() : null).toArray(String[]::new));
        }
        if( end!=null ){
            a.setEndLabels(Arrays.stream(end).map(s -> s!=null ? s.toString() : null).toArray(String[]::new));
        }
        a.setIndex(index);
        a.setDescriptor(descriptor);
        a.setVisible(visible);

        emit(a);
        return dump;
    }

    /**
     * Visits a line number declaration.
     *
     * @param line  a line number. This number refers to the source file from which the class was
     *              compiled.
     * @param start the first instruction corresponding to this line number.
     * @throws IllegalArgumentException if {@code start} has not already been visited by this visitor
     *                                  (by the {@link #visitLabel} method).
     */
    @Override
    public void visitLineNumber(int line, Label start){
        emit(new MLineNumber(line,start.toString()));
    }

    /**
     * Visits the maximum stack size and the maximum number of local variables of the method.
     *
     * @param maxStack  maximum stack size of the method.
     * @param maxLocals maximum number of local variables for the method.
     */
    @Override
    public void visitMaxs(int maxStack, int maxLocals){
        emit(new MMaxs(maxStack,maxLocals));
    }

    /**
     * Visits the end of the method. This method, which is the last one to be called, is used to
     * inform the visitor that all the annotations and attributes of the method have been visited.
     */
    @Override
    public void visitEnd(){
        emit(new MEnd());
    }
}
