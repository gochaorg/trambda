package xyz.cofe.trambda.bc.mth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.cofe.trambda.bc.ByteCode;

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
 * <ul>
 *     <li>
 *         {@link #type}
 *
 *         the type of this stack map frame. Must be {@link Opcodes#F_NEW} for expanded
 *         frames, or {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link
 *         Opcodes#F_SAME} or {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
 *     </li>
 *     <li>
 *         {@link #numLocal}
 *         the number of local variables in the visited frame.
 *     </li>
 *     <li>
 *         {@link #local}
 *         the local variable types in this frame. This array must not be modified. Primitive
 *         types are represented by {@link Opcodes#TOP}, {@link Opcodes#INTEGER}, {@link
 *         Opcodes#FLOAT}, {@link Opcodes#LONG}, {@link Opcodes#DOUBLE}, {@link Opcodes#NULL} or
 *         {@link Opcodes#UNINITIALIZED_THIS} (long and double are represented by a single element).
 *         Reference types are represented by String objects (representing internal names), and
 *         uninitialized types by Label objects (this label designates the NEW instruction that
 *         created this uninitialized value).
 *     </li>
 *     <li>
 *         {@link #numStack}
 *         the number of operand stack elements in the visited frame.
 *     </li>
 *     <li>
 *         {@link #stack}
 *         the operand stack types in this frame. This array must not be modified. Its
 *         content has the same format as the "local" array.
 *     </li>
 * </ul>
 *
 * <b>throws IllegalStateException</b>
 * if a frame is visited just after another one, without any
 * instruction between the two (unless this frame is a Opcodes#F_SAME frame, in which case it
 * is silently ignored).
 */
public class MFrame extends MAbstractBC implements MethodWriter {
//    //region ElemType
//    public static enum ElemType {
//        Top(Opcodes.TOP),
//        Integer(Opcodes.INTEGER),
//        Float(Opcodes.FLOAT),
//        Long(Opcodes.LONG),
//        Double(Opcodes.DOUBLE),
//        Null(Opcodes.NULL),
//        UninitializedThis(Opcodes.UNINITIALIZED_THIS);
//        public final int code;
//        ElemType(int code){
//            this.code = code;
//        }
//    }
//    //endregion

    public MFrame(){}
    public MFrame(int type, int numLocal, List<Object> local, int numStack, List<Object> stack){
        this.type = type;
        this.numLocal = numLocal;
        this.local = local;
        this.numStack = numStack;
        this.stack = stack;
    }
    public MFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack){
        this.type = type;
        this.numLocal = numLocal;
        Function<Object[],List<Object>> init = (arr) -> {
            if( arr==null )return null;
            var res = new ArrayList<Object>();
            for( var l : arr ){
                if( l==null ){
                    res.add(null);
                }else{
                    if( l instanceof Serializable ){
                        res.add(l);
                    }else{
                        throw new IllegalArgumentException("not serializable");
                    }
//                    if( Objects.equals(l,Opcodes.TOP) ){
//                        res.add(ElemType.Top);
//                    }else if( Objects.equals(l,Opcodes.INTEGER) ){
//                        res.add(ElemType.Integer);
//                    }else if( Objects.equals(l,Opcodes.FLOAT) ){
//                        res.add(ElemType.Float);
//                    }else if( Objects.equals(l,Opcodes.LONG) ){
//                        res.add(ElemType.Long);
//                    }else if( Objects.equals(l,Opcodes.DOUBLE) ){
//                        res.add(ElemType.Double);
//                    }else if( Objects.equals(l,Opcodes.NULL) ){
//                        res.add(ElemType.Null);
//                    }else if( Objects.equals(l,Opcodes.UNINITIALIZED_THIS) ){
//                        res.add(ElemType.UninitializedThis);
//                    }else {
//                        throw new IllegalArgumentException("undefined type in frame "+l);
//                    }
                }
            }
            return res;
        };
        this.local = init.apply(local);
        this.numStack = numStack;
        this.stack = init.apply(stack);
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MFrame(MFrame sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        type = sample.getType();
        numLocal = sample.getNumLocal();
        if( sample.local!=null )this.local = new ArrayList<>(sample.local);
        if( sample.stack!=null )this.stack = new ArrayList<>(sample.stack);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MFrame clone(){ return new MFrame(this); }

    //region type:int
    private int type;
    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
    //endregion
    //region numLocal:int
    private int numLocal;
    public int getNumLocal(){
        return numLocal;
    }
    public void setNumLocal(int numLocal){
        this.numLocal = numLocal;
    }
    //endregion
    //region local:List<ElemType>
    private List<Object> local;
    public List<Object> getLocal(){
        return local;
    }
    public void setLocal(List<Object> local){
        this.local = local;
    }
    //endregion
    //region numStack:int
    private int numStack;
    public int getNumStack(){
        return numStack;
    }
    public void setNumStack(int numStack){
        this.numStack = numStack;
    }
    //endregion
    //region stack:List<ElemType>
    private List<Object> stack;
    public List<Object> getStack(){
        return stack;
    }
    public void setStack(List<Object> stack){
        this.stack = stack;
    }
    //endregion

    public String toString(){
        return MFrame.class.getSimpleName()+
            " type="+type+
            " numLocal="+numLocal+
            " local="+(local==null ? "null" : local)+
            " numStack="+numStack+", stack="+(stack==null ? "null" : stack)+
            "";
    }

    @Override
    public void write(MethodVisitor mv, MethodWriterCtx ctx){
        if( mv==null )throw new IllegalArgumentException( "mv==null" );

        Object[] local = getLocal()==null ? null : new Object[getLocal().size()];
        if( local!=null ){
            for( int i=0;i<local.length;i++ ){
                local[i] = getLocal().get(i);
            }
        }
        Object[] stack = getStack()==null ? null : new Object[getStack().size()];
        if( stack!=null ){
            for( int i=0;i<stack.length;i++ ){
                stack[i] = getStack().get(i);
            }
        }
        mv.visitFrame(getType(),getNumLocal(),local,getNumStack(),stack);
    }
}
