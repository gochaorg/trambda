package xyz.cofe.trambda.bc.mth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <a href="https://coderoad.ru/25109942/%D0%A7%D1%82%D0%BE-%D1%82%D0%B0%D0%BA%D0%BE%D0%B5-%D1%84%D1%80%D0%B5%D0%B9%D0%BC-%D0%BA%D0%B0%D1%80%D1%82%D1%8B-%D1%81%D1%82%D0%B5%D0%BA%D0%B0">Java требует проверки всех загруженных классов, чтобы обеспечить безопасность песочницы и обеспечить безопасность кода для оптимизации. Обратите внимание, что это делается на уровне байт-кода , поэтому проверка не проверяет инварианты языка Java, она просто проверяет, что байт-код имеет смысл в соответствии с правилами байт-кода.</a>
 * <p> Помимо прочего, проверка байт-кода гарантирует, что инструкции хорошо сформированы, что все переходы являются допустимыми инструкциями в методе и что все инструкции работают со значениями правильного типа. В последнем случае используется карта стека.
 * <p> Дело в том, что байт-код сам по себе не содержит явной информации о типе. Типы определяются неявно с помощью анализа потоков данных. Например, инструкция iconst создает целочисленное значение. Если вы храните его в слоте 1, этот слот теперь имеет int. Если поток управления сливается из кода, в котором вместо этого хранится float, слот теперь считается недопустимым типом, что означает, что вы больше ничего не можете сделать с этим значением, пока не перезапишете его.
 * <p> Исторически верификатор байт-кода выводил все типы, используя эти правила потока данных. К сожалению, невозможно вывести все типы за один линейный проход через байт-код, поскольку обратный переход может привести к недействительности уже выведенных типов. Классический верификатор решил эту проблему, повторяя код до тех пор, пока все не перестанет меняться, что потенциально потребует нескольких проходов.
 * <p> Однако проверка замедляет загрузку класса в Java. Oracle решил решить эту проблему, добавив новый, более быстрый верификатор, который может проверять байт-код за один проход. Для этого им потребовалось, чтобы все новые классы, начиная с Java 7 (с Java 6 в переходном состоянии), несли метаданные об их типах, чтобы байт-код можно было проверить за один проход. Поскольку сам формат байт-кода не может быть изменен, эта информация о типе хранится отдельно в атрибуте с именем StackMapTable .
 * <p> Простое хранение типа для каждого отдельного значения в каждой отдельной точке кода, очевидно, заняло бы много места и было бы очень расточительным. Чтобы сделать метаданные меньше и эффективнее, они решили, что в них будут перечислены только типы в позициях, которые являются целями прыжков . Если вы подумаете об этом, это единственный раз, когда вам нужна дополнительная информация для проверки одного прохода. В промежутках между целями прыжка весь поток управления является линейным, поэтому вы можете выводить типы в промежуточных позициях, используя старые правила вывода.
 * <p> Каждая позиция, в которой явно перечислены типы, называется фреймом карты стека. Атрибут StackMapTable содержит список кадров по порядку, хотя они обычно выражаются как отличие от предыдущего кадра, чтобы уменьшить размер данных. Если в методе нет фреймов, что происходит, когда поток управления никогда не соединяется (т. Е. CFG-это дерево), то атрибут StackMapTable может быть полностью опущен.
 * <p> Итак, это основная идея о том, как работает StackMapTable и почему он был добавлен. Последний вопрос заключается в том, как создается неявный начальный фрейм. Ответ, конечно, заключается в том, что в начале метода стек операндов пуст, а слоты локальных переменных имеют типы, заданные типами параметров метода, которые определяются из декриптора метода.
 * <p> Если вы привыкли к Java, есть несколько незначительных различий в том, как типы параметров метода работают на уровне байт-кода. Во-первых, виртуальные методы имеют неявный this в качестве первого параметра. Во - вторых, boolean , byte , char и short не существуют на уровне байт-кода. Вместо этого все они реализуются как внутренние за кулисами.
 * 
 * <hr>
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.6">Фрейм используется для хранения данных и частичных результатов, а также для выполнения динамического связывания, возврата значений для методов и отправки исключений</a>.
 * <p> Новый фрейм создается каждый раз при вызове метода. Кадр уничтожается, когда завершается вызов его метода, независимо от того, является ли это завершение нормальным или внезапным (он вызывает неперехваченное исключение). Фреймы выделяются из стека виртуальной машины Java (§2.5.2) потока, создающего фрейм. Каждый фрейм имеет свой собственный массив локальных переменных (§2.6.1), свой собственный стек операндов (§2.6.2) и ссылку на пул констант времени выполнения (§2.5.5) класса текущего метода. .
 * <p> Кадр может быть расширен дополнительной информацией, зависящей от реализации, например, отладочной информацией.
 * <p> Размеры массива локальных переменных и стека операндов определяются во время компиляции и предоставляются вместе с кодом для метода, связанного с кадром (§4.7.3). Таким образом, размер структуры данных кадра зависит только от реализации виртуальной машины Java, и память для этих структур может быть выделена одновременно с вызовом метода.
 * <p> Только один фрейм, фрейм для выполняемого метода, активен в любой точке данного потока управления. Этот кадр называется текущим кадром, а его метод известен как текущий метод. Класс, в котором определен текущий метод, является текущим классом. Операции с локальными переменными и стеком операндов обычно относятся к текущему кадру.
 * <p> Фрейм перестает быть текущим, если его метод вызывает другой метод или его метод завершается. Когда вызывается метод, создается новый фрейм, который становится текущим, когда управление передается новому методу. При возврате метода текущий фрейм передает результат своего вызова метода, если таковой имеется, в предыдущий фрейм. Затем текущий кадр отбрасывается, так как предыдущий кадр становится текущим.
 * <p> Обратите внимание, что кадр, созданный потоком, является локальным для этого потока и не может ссылаться на какой-либо другой поток.
 * 
 * <p>
 * Этот инструкция вставляется непосредственно перед любой инструкцией i, 
 * которая следует за инструкцией безусловного перехода, 
 * такой как GOTO или THROW, 
 * 
 * <hr>
 * 
 * которая является целью инструкции перехода или запускает блок обработчика исключений. 
 * Посещаемые типы должны описывать значения локальных переменных и элементов 
 * стека операндов непосредственно перед выполнением i. (*) 
 * 
 * это обязательно только для классов, версия которых больше или равна Opcodes.V1_6. 
 * Кадры метода должны быть указаны либо в развернутой форме, либо в сжатом виде 
 * (все кадры должны использовать один и тот же формат, 
 * т.е. вы не должны смешивать развернутые и сжатые кадры в одном методе)
 * 
 * <p>
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

    /**
     * Конструктор по умолчанию
     */
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
