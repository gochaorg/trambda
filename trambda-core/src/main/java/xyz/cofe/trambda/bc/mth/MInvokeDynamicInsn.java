package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.LongArg;

/**
 * <a href="https://habr.com/ru/post/328240/">Хорошая статья о Что там с JEP-303 или изобретаем invokedynamic</a>
 * 
 * <hr>
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.invokedynamic">Инструкция invokedynamic</a>
 * 
 * <p>
 * Operation
 * 
 * <p>
 * Invoke dynamic method
 * 
 * <p>
 * Format
<pre>
invokedynamic
indexbyte1
indexbyte2
0
0
</pre>
* 
* Forms
* <p> invokedynamic = 186 (0xba)
* 
* <p> Operand Stack
<pre>..., [arg1, [arg2 ...]] → ...</pre>
* 
* <p> Description
* <p> Each specific lexical occurrence of an invokedynamic instruction is called a dynamic call site.
* <p> Каждое конкретное лексическое вхождение вызванной динамической инструкции называется динамическим сайтом вызова.
* 
* <p> First, the unsigned indexbyte1 and indexbyte2 are used to construct an index into 
* the run-time constant pool of the current class (§2.6), where the value of the index is 
* (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that 
* index must be a symbolic reference to a call site specifier (§5.1). 
* The values of the third and fourth operand bytes must always be zero.
* <p> Во-первых, беззнаковые indexbyte1 и indexbyte2 используются для создания 
* индекса в пуле постоянных времени выполнения текущего класса (§2.6), где значение индекса 
* (indexbyte1 &lt;&lt; 8) | indexbyte2. Элемент пула констант времени 
* выполнения в этом индексе должен быть символьной ссылкой на спецификатор сайта вызова (§5.1). 
* Значения третьего и четвертого байтов операнда всегда должны быть нулевыми.
* 
* <p> The call site specifier is resolved (§5.4.3.6) for this specific dynamic 
* call site to obtain a reference to a java.lang.invoke.MethodHandle instance, 
* a reference to a java.lang.invoke.MethodType instance, and references to static arguments.
* <p> Спецификатор сайта вызова разрешен (§5.4.3.6) для этого конкретного 
* сайта динамического вызова, чтобы получить ссылку на экземпляр 
* java.lang.invoke.MethodHandle, ссылку на экземпляр java.lang.invoke.MethodType 
* и ссылки на статический аргументы.
* 
* <p> Next, as part of the continuing resolution of the call site specifier, 
* the bootstrap method is invoked as if by execution of an invokevirtual instruction 
* (§invokevirtual) that contains a run-time constant pool index to a symbolic reference 
* to a method (§5.1) with the following properties:
* <p>
* Затем, как часть продолжающегося разрешения спецификатора сайта вызова, 
* метод начальной загрузки вызывается, как если бы путем выполнения инструкции 
* invokevirtual (§invokevirtual), которая содержит индекс пула констант времени 
* выполнения для символической ссылки на метод (§5.1) ) со следующими свойствами:
* 
* <ul>
* <li> The method's name is invoke;
*      <br> Вызывается имя метода;
* 
* <li> The method's descriptor has a return type of java.lang.invoke.CallSite;
*       <br> Дескриптор метода имеет возвращаемый тип java.lang.invoke.CallSite;
* 
* <li> The method's descriptor has parameter types derived from the items pushed on to the operand stack, as follows.
*       <br> Дескриптор метода имеет типы параметров, производные от элементов, помещенных в стек операндов, как показано ниже.
* 
* <li> The first four parameter types in the descriptor are java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandles.Lookup, String, and java.lang.invoke.MethodType, in that order.
*       <br> Первые четыре типа параметров в дескрипторе - это java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandles.Lookup, String и java.lang.invoke.MethodType в указанном порядке.
* 
* <li> If the call site specifier has any static arguments, then a parameter type for each argument is appended to the parameter types of the method descriptor in the order that the arguments were pushed on to the operand stack. These parameter types may be Class, java.lang.invoke.MethodHandle, java.lang.invoke.MethodType, String, int, long, float, or double.
*       <br> Если спецификатор сайта вызова имеет какие-либо статические аргументы, то тип параметра для каждого аргумента добавляется к типам параметров дескриптора метода в том порядке, в котором аргументы были помещены в стек операндов. Эти типы параметров могут быть Class, java.lang.invoke.MethodHandle, java.lang.invoke.MethodType, String, int, long, float или double.
* 
* <li> The method's symbolic reference to the class in which the method is to be found indicates the class java.lang.invoke.MethodHandle.
*       <br> Символьная ссылка метода на класс, в котором должен быть найден метод, указывает на класс java.lang.invoke.MethodHandle.
* </ul>
* 
* <p> where it is as if the following items were pushed, in order, onto the operand stack:
* <br> где это как если бы следующие элементы были помещены в стек операндов по порядку:
* 
* <ul>
* <li> the reference to the java.lang.invoke.MethodHandle object for the bootstrap method;
*   <br> ссылка на объект java.lang.invoke.MethodHandle для метода начальной загрузки;
* 
* <li> a reference to a java.lang.invoke.MethodHandles.Lookup object for the class in which this dynamic call site occurs;
* <br> ссылка на объект java.lang.invoke.MethodHandles.Lookup для класса, в котором происходит этот сайт динамического вызова;
* 
* <li> a reference to the String for the method name in the call site specifier;
* <br> ссылка на String для имени метода в спецификаторе сайта вызова;
* 
* <li> the reference to the java.lang.invoke.MethodType object obtained for the method descriptor in the call site specifier;
* <br> ссылка на объект java.lang.invoke.MethodType, полученная для дескриптора метода в спецификаторе сайта вызова;
* 
* <li> references to classes, method types, method handles, and string literals denoted as static arguments in the call site specifier, and numeric values (§2.3.1, §2.3.2) denoted as static arguments in the call site specifier, in the order in which they appear in the call site specifier. (That is, no boxing occurs for primitive values.)
* <br> ссылки на классы, типы методов, дескрипторы методов и строковые литералы, обозначенные как статические аргументы в спецификаторе сайта вызова, и числовые значения (§2.3.1, §2.3.2), обозначенные как статические аргументы в спецификаторе места вызова, в порядке в котором они появляются в указателе места вызова. (То есть для примитивных значений упаковка не выполняется.)
* </ul>
* 
* <p> As long as the bootstrap method can be correctly invoked by the invoke method, its descriptor is arbitrary. For example, the first parameter type could be Object instead of java.lang.invoke.MethodHandles.Lookup, and the return type could also be Object instead of java.lang.invoke.CallSite.
* <br> Пока метод начальной загрузки может быть правильно вызван методом invoke, его дескриптор является произвольным. Например, первым типом параметра может быть Object вместо java.lang.invoke.MethodHandles.Lookup, а типом возвращаемого значения также может быть Object вместо java.lang.invoke.CallSite.
* 
* <p> If the bootstrap method is a variable arity method, then some or all of the arguments on the operand stack specified above may be collected into a trailing array parameter.
* <br> Если метод начальной загрузки является методом переменной арности, то некоторые или все аргументы в стеке операндов, указанные выше, могут быть собраны в завершающий параметр массива.
* 
* <p> The invocation of a bootstrap method occurs within a thread that is attempting resolution of the symbolic reference to the call site specifier of this dynamic call site. If there are several such threads, the bootstrap method may be invoked in several threads concurrently. Therefore, bootstrap methods which access global application data must take the usual precautions against race conditions.
* <br> Вызов метода начальной загрузки происходит в потоке, который пытается разрешить символическую ссылку на спецификатор сайта вызова этого сайта динамического вызова. Если таких потоков несколько, метод начальной загрузки может быть вызван в нескольких потоках одновременно. Следовательно, методы начальной загрузки, которые обращаются к глобальным данным приложения, должны принимать обычные меры предосторожности против условий гонки.
* 
* <p> The result returned by the bootstrap method must be a reference to an object whose class is java.lang.invoke.CallSite or a subclass of java.lang.invoke.CallSite. This object is known as the call site object. The reference is popped from the operand stack used as if in the execution of an invokevirtual instruction.
* <br> Результат, возвращаемый методом начальной загрузки, должен быть ссылкой на объект, класс которого java.lang.invoke.CallSite или подкласс java.lang.invoke.CallSite. Этот объект известен как объект сайта вызова. Ссылка извлекается из стека операндов, используемого, как если бы при выполнении команды invokevirtual.
* 
* <p> If several threads simultaneously execute the bootstrap method for the same dynamic call site, the Java Virtual Machine must choose one returned call site object and install it visibly to all threads. Any other bootstrap methods executing for the dynamic call site are allowed to complete, but their results are ignored, and the threads' execution of the dynamic call site proceeds with the chosen call site object.
* <br> Если несколько потоков одновременно выполняют метод начальной загрузки для одного и того же сайта динамического вызова, виртуальная машина Java должна выбрать один возвращенный объект сайта вызова и установить его видимым образом для всех потоков. Любые другие методы начальной загрузки, выполняемые для сайта динамического вызова, могут завершиться, но их результаты игнорируются, и выполнение потоков на сайте динамического вызова продолжается с выбранным объектом сайта вызова.
* 
* <p> The call site object has a type descriptor (an instance of java.lang.invoke.MethodType) which must be semantically equal to the java.lang.invoke.MethodType object obtained for the method descriptor in the call site specifier.
* <br> Объект сайта вызова имеет дескриптор типа (экземпляр java.lang.invoke.MethodType), который должен быть семантически равен объекту java.lang.invoke.MethodType, полученному для дескриптора метода в спецификаторе сайта вызова.
* 
* <p> The result of successful call site specifier resolution is a call site object which is permanently bound to the dynamic call site.
* <br> Результатом успешного разрешения спецификатора сайта вызова является объект сайта вызова, который постоянно привязан к динамическому сайту вызова.
* 
* <p> The method handle represented by the target of the bound call site object is invoked. The invocation occurs as if by execution of an invokevirtual instruction (§invokevirtual) that indicates a run-time constant pool index to a symbolic reference to a method (§5.1) with the following properties:
* <br> Вызывается дескриптор метода, представленный целью привязанного объекта сайта вызова. Вызов происходит, как если бы при выполнении инструкции invokevirtual (§invokevirtual), которая указывает индекс пула констант времени выполнения на символическую ссылку на метод (§5.1) со следующими свойствами:
* 
* <ul>
* <li> The method's name is invokeExact;
* <br> Имя метода - invokeExact;
* 
* <li> The method's descriptor is the method descriptor in the call site specifier; and
* <br> Дескриптор метода - это дескриптор метода в спецификаторе сайта вызова; а также
* 
* <li> The method's symbolic reference to the class in which the method is to be found indicates the class java.lang.invoke.MethodHandle.
* <br> Символьная ссылка метода на класс, в котором должен быть найден метод, указывает на класс java.lang.invoke.MethodHandle.
* </ul>
* 
* <p> The operand stack will be interpreted as containing a reference to the target of the call site object, followed by nargs argument values, where the number, type, and order of the values must be consistent with the method descriptor in the call site specifier.
* <br> Стек операндов будет интерпретироваться как содержащий ссылку на цель объекта сайта вызова, за которой следуют значения аргумента nargs, где число, тип и порядок значений должны соответствовать дескриптору метода в спецификаторе сайта вызова.
* 
* <p><b>Linking Exceptions</b>
* <p> If resolution of the symbolic reference to the call site specifier throws an exception E, the invokedynamic instruction throws a BootstrapMethodError that wraps E.
* <br> Если разрешение символьной ссылки на спецификатор сайта вызова вызывает исключение E, инструкция invokedynamic генерирует ошибку BootstrapMethodError, которая обертывает E.
* 
* <p> Otherwise, during the continuing resolution of the call site specifier, if invocation of the bootstrap method completes abruptly (§2.6.5) because of a throw of exception E, the invokedynamic instruction throws a BootstrapMethodError that wraps E. (This can occur if the bootstrap method has the wrong arity, parameter type, or return type, causing java.lang.invoke.MethodHandle . invoke to throw java.lang.invoke.WrongMethodTypeException.)
* <br> В противном случае, во время продолжающегося разрешения спецификатора сайта вызова, если вызов метода начальной загрузки завершается внезапно (§2.6.5) из-за выброса исключения E, инструкция invokedynamic выдает ошибку BootstrapMethodError, которая обертывает E. (Это может произойти, если Метод начальной загрузки имеет неправильную арность, тип параметра или возвращаемый тип, в результате чего java.lang.invoke.MethodHandle. invoke вызывает исключение java.lang.invoke.WrongMethodTypeException.)
* 
* <p> Otherwise, during the continuing resolution of the call site specifier, if the result from the bootstrap method invocation is not a reference to an instance of java.lang.invoke.CallSite, the invokedynamic instruction throws a BootstrapMethodError.
* <br> В противном случае, во время продолжающегося разрешения спецификатора сайта вызова, если результат вызова метода начальной загрузки не является ссылкой на экземпляр java.lang.invoke.CallSite, инструкция invokedynamic выдает ошибку BootstrapMethodError.
* 
* <p> Otherwise, during the continuing resolution of the call site specifier, if the type descriptor of the target of the call site object is not semantically equal to the method descriptor in the call site specifier, the invokedynamic instruction throws a BootstrapMethodError.
* <br> В противном случае, во время продолжающегося разрешения спецификатора сайта вызова, если дескриптор типа цели объекта сайта вызова семантически не равен дескриптору метода в описателе сайта вызова, инструкция invokedynamic выдает ошибку BootstrapMethodError.
* 
* <p><b>Run-time Exceptions</b>
* <p> If this specific dynamic call site completed resolution of its call site specifier, it implies that a non-null reference to an instance of java.lang.invoke.CallSite is bound to this dynamic call site. Therefore, the operand stack item which represents a reference to the target of the call site object is never null. Similarly, it implies that the method descriptor in the call site specifier is semantically equal to the type descriptor of the method handle to be invoked as if by execution of an invokevirtual instruction.
* <br> Если этот конкретный сайт динамического вызова завершил разрешение своего спецификатора сайта вызова, это означает, что ненулевая ссылка на экземпляр java.lang.invoke.CallSite привязана к этому сайту динамического вызова. Следовательно, элемент стека операндов, который представляет ссылку на цель объекта сайта вызова, никогда не имеет значения NULL. Точно так же это подразумевает, что дескриптор метода в спецификаторе сайта вызова семантически равен дескриптору типа дескриптора метода, который должен быть вызван, как если бы при выполнении команды invokevirtual.
* 
* <p> These invariants mean that an invokedynamic instruction which is bound to a call site object never throws a NullPointerException or a java.lang.invoke.WrongMethodTypeException.
* <br> Эти инварианты означают, что invokedynamic инструкция, которая привязана к объекту сайта вызова, никогда не вызывает исключение NullPointerException или java.lang.invoke.WrongMethodTypeException.
 */
public class MInvokeDynamicInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MInvokeDynamicInsn(){
    }

    public MInvokeDynamicInsn(
        String name,
        String descriptor,
        org.objectweb.asm.Handle handle,
        Object[] args
    ){
        this.name = name;
        this.descriptor = descriptor;
        if( handle!=null ){
            this.bootstrapMethodHandle = new MHandle(handle);
        }

        if(args!=null){
            bootstrapMethodArguments = new ArrayList<>();
            for( var a : args ){
                if( a instanceof Integer ){
                    bootstrapMethodArguments.add(new IntArg((Integer) a));
                }else if( a instanceof Float ){
                    bootstrapMethodArguments.add(new FloatArg((Float) a));
                }else if( a instanceof Long ){
                    bootstrapMethodArguments.add(new LongArg((Long) a));
                }else if( a instanceof Double ){
                    bootstrapMethodArguments.add(new DoubleArg((Double) a));
                }else if( a instanceof String ){
                    bootstrapMethodArguments.add(new StringArg((String) a));
                }else if( a instanceof Type ){
                    bootstrapMethodArguments.add(new TypeArg(((Type)a).toString()));
                }else if( a instanceof org.objectweb.asm.Handle ){
                    bootstrapMethodArguments.add(new HandleArg(new MHandle((org.objectweb.asm.Handle)a)));
                }else {
                    throw new IllegalArgumentException("unsupported bootstrapMethodArgument "+a);
                }
            }
        }
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MInvokeDynamicInsn(MInvokeDynamicInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.getName();
        descriptor = sample.getDescriptor();

        var bmh = sample.bootstrapMethodHandle;
        if( bmh!=null )bootstrapMethodHandle = bmh.clone();

        var l = sample.bootstrapMethodArguments;
        if( l!=null ){
            bootstrapMethodArguments = new ArrayList<>(l);
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MInvokeDynamicInsn clone(){ return new MInvokeDynamicInsn(this); }

    //region name : String
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String
    private String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region bootstrapMethodHandle : MHandle
    private MHandle bootstrapMethodHandle;
    public MHandle getBootstrapMethodHandle(){
        return bootstrapMethodHandle;
    }
    public void setBootstrapMethodHandle(MHandle bootstrapMethodHandle){
        this.bootstrapMethodHandle = bootstrapMethodHandle;
    }
    //endregion
    //region bootstrapMethodArguments : List<BootstrapMethArg>
    private List<BootstrapMethArg> bootstrapMethodArguments;
    public List<BootstrapMethArg> getBootstrapMethodArguments(){
        if( bootstrapMethodArguments==null ){
            bootstrapMethodArguments = new ArrayList<>();
        }
        return bootstrapMethodArguments;
    }
    public void setBootstrapMethodArguments(List<BootstrapMethArg> bootstrapMethodArguments){
        this.bootstrapMethodArguments = bootstrapMethodArguments;
    }
    //endregion

    public String toString(){
        return MInvokeDynamicInsn.class.getSimpleName()+
            " name="+name+
            " descriptor="+descriptor+
            " bootstrapMethodHandle="+bootstrapMethodHandle+
            " args="+bootstrapMethodArguments+"";
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var bmh = getBootstrapMethodHandle();
        if( bmh==null )throw new IllegalStateException("getBootstrapMethodHandle() return null");

        var hdl = new org.objectweb.asm.Handle(
            bmh.getTag(),
            bmh.getOwner(),
            bmh.getName(),
            bmh.getDesc(),
            bmh.isIface()
        );

        var bma = getBootstrapMethodArguments();
        if( bma==null ){
            throw new IllegalStateException("getBootstrapMethodArguments() return null");
        }

        Object[] args = new Object[bma.size()];
        for( int ai=0; ai<args.length; ai++ ){
            Object arg = null;
            var sarg = bma.get(ai);
            if( sarg instanceof IntArg ){
                arg = build((IntArg) sarg);
            }else if( sarg instanceof StringArg ){
                arg = build((StringArg)sarg);
            }else if( sarg instanceof FloatArg ){
                arg = build((FloatArg) sarg);
            }else if( sarg instanceof LongArg ){
                arg = build((LongArg) sarg);
            }else if( sarg instanceof DoubleArg ){
                arg = build((DoubleArg) sarg);
            }else if( sarg instanceof TypeArg ){
                arg = build((TypeArg)sarg);
            }else if( sarg instanceof HandleArg ){
                arg = build((HandleArg)sarg, hdl, ctx);
            }else {
                throw new UnsupportedOperationException("can't feetch BootstrapMethodArgument from "+sarg);
            }
            args[ai] = arg;
        }

        v.visitInvokeDynamicInsn(getName(),getDescriptor(),hdl,args);
    }

    protected Object build(IntArg arg){ return arg.getValue(); }
    protected Object build(LongArg arg){ return arg.getValue(); }
    protected Object build(FloatArg arg){ return arg.getValue(); }
    protected Object build(DoubleArg arg){ return arg.getValue(); }
    protected Object build(StringArg arg){ return arg.getValue(); }
    protected Object build(TypeArg arg){ return Type.getType(arg.getType()); }
    protected Object build(HandleArg arg, org.objectweb.asm.Handle bm, MethodWriterCtx ctx){
        var hdl = arg.getHandle();
        if( hdl==null )throw new IllegalArgumentException("target handle is null");
        return ctx.bootstrapArgument(hdl, bm);
    }
}
