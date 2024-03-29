package xyz.cofe.trambda.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.objectweb.asm.Opcodes.*;

/**
 * Флаги методов и инструкций
 */
public class AccFlags {
    private final int flags;
    public AccFlags(int flags){
        this.flags = flags;
    }
    public int value(){ return flags; }

    private static Map<String,Integer> flagName = new TreeMap<>();
    public static Map<String,Integer> flagName(){
        return Collections.unmodifiableMap(flagName);
    }

    private boolean has(int flag){
        return (flags & flag) == flag;
    }
    private static int set(int flags, int flag){
        return flags | flag;
    }
    private static int set(int flags, int flag, boolean switchOn){
        return switchOn ? (flags | flag) : (flags & ~flag);
    }
    private static int unset(int flags,int flag){
        return flags & ~flag;
    }

    //region 0x00001 : public  - Объявлен публичным; может быть доступен извне пакета. (class, field, method)
    /**
     * Declared public; may be accessed from outside its package.
     * <br>
     * Объявлен публичным; может быть доступен извне пакета.
     *
     * <p> Применимо к : class, field, method
     * @return is public
     */
    public boolean isPublic(){ return has(ACC_PUBLIC); }

    /**
     * Declared public; may be accessed from outside its package.
     * <br>
     * Объявлен публичным; может быть доступен извне пакета.
     *
     * <p> Применимо к : class, field, method
     * @param v as public
     * @return flags
     */
    public AccFlags withPublic(boolean v){ return new AccFlags(set(flags,ACC_PUBLIC,v)); }
    static { flagName.put("Public", ACC_PUBLIC); }
    //endregion
    //region 0x00002 : private - Объявлен частным; доступен только внутри определяющего класса. (class, field, method)
    /**
     * Declared private; accessible only within the defining class.
     *
     * <p> Объявлен частным; доступен только внутри определяющего класса.
     *
     * <p> Применимо к : class, field, method
     *
     * @return is private
     */
    public boolean isPrivate(){ return has(ACC_PRIVATE); }

    /**
     * Declared private; accessible only within the defining class.
     *
     * <p> Объявлен частным; доступен только внутри определяющего класса.
     *
     * <p> Применимо к : class, field, method
     *
     * @param v as private
     * @return flags
     */
    public AccFlags withPrivate(boolean v){ return new AccFlags(set(flags,ACC_PRIVATE,v)); }
    static { flagName.put("Private", ACC_PRIVATE); }
    //endregion
    //region 0x00004 : protected - Объявлен защищенным; могут быть доступны внутри подклассов. (class, field, method)
    /**
     * Declared protected; may be accessed within subclasses.
     *
     * <p> Объявлен защищенным; могут быть доступны внутри подклассов.
     *
     * <p> Применимо к : class, field, method
     *
     * @return is protected
     */
    public boolean isProtected(){ return has(ACC_PROTECTED); }

    /**
     * Declared protected; may be accessed within subclasses.
     *
     * <p> Объявлен защищенным; могут быть доступны внутри подклассов.
     *
     * <p> Применимо к : class, field, method
     * @param v as protected
     * @return flags
     */
    public AccFlags withProtected(boolean v){ return new AccFlags(set(flags,ACC_PROTECTED,v)); }
    static { flagName.put("Protected", ACC_PROTECTED); }
    //endregion
    //region 0x00008 : static (field, method)
    /**
     * Declared static.
     *
     * <p> Применимо к : field, method
     * @return is static
     */
    public boolean isStatic(){ return has(ACC_STATIC); }

    /**
     * Declared static.
     *
     * <p> Применимо к : field, method
     * @param v as static
     * @return flags
     */
    public AccFlags withStatic(boolean v){ return new AccFlags(set(flags,ACC_STATIC,v)); }
    static { flagName.put("Static", ACC_STATIC); }
    //endregion
    //region 0x00010 : final - Объявлен финальным; подклассы не допускаются. (class, field, method, parameter)
    /**
     * Declared final; no subclasses allowed.
     * <p>
     *     Объявлен финальным; подклассы не допускаются.
     *
     * <p> Применимо к : class, field, method, parameter
     * @return is final
     */
    public boolean isFinal(){ return has(ACC_FINAL); }

    /**
     * Declared final; no subclasses allowed.
     * <p>
     *     Объявлен финальным; подклассы не допускаются.
     *
     * <p> Применимо к : class, field, method, parameter
     * @param v as final
     * @return flags
     */
    public AccFlags withFinal(boolean v){ return new AccFlags(set(flags,ACC_FINAL,v)); }
    static { flagName.put("Final", ACC_FINAL); }
    //endregion
    //region 0x00020 : open - Указывает, что этот модуль открыт. (module)
    /**
     * Indicates that this module is open.
     *
     * <p> Указывает, что этот модуль открыт.
     *
     * <p> Применимо к : module
     * @return is open
     */
    public boolean isOpen(){ return has(ACC_OPEN); }

    /**
     * Indicates that this module is open.
     *
     * <p> Указывает, что этот модуль открыт.
     *
     * <p> Применимо к : module
     * @param v as open module
     * @return flags
     */
    public AccFlags withOpen(boolean v){ return new AccFlags(set(flags,ACC_OPEN,v)); }
    static { flagName.put("Open", ACC_OPEN); }
    //endregion
    //region 0x00020 : super - Особо обрабатывайте методы суперкласса, когда они вызываются инструкцией invokespecial. (class)
    /**
     * Treat superclass methods specially when invoked by the invokespecial instruction.
     * <p>
     *     Особо обрабатывайте методы суперкласса, когда они вызываются инструкцией invokespecial.
     *
     * <p> Применимо к : class
     *
     * @return is super
     */
    public boolean isSuper(){ return has(ACC_SUPER); }

    /**
     * Treat superclass methods specially when invoked by the invokespecial instruction.
     * <p>
     *     Особо обрабатывайте методы суперкласса, когда они вызываются инструкцией invokespecial.
     *
     * <p> Применимо к : class
     * @param v as super
     * @return flags
     */
    public AccFlags withSuper(boolean v){ return new AccFlags(set(flags,ACC_SUPER,v)); }
    static { flagName.put("Super", ACC_SUPER); }
    //endregion
    //region 0x00020 : synchronized - Заявлен как синхронизированный; вызов завершается использованием монитора. (method)
    /**
     * Declared synchronized; invocation is wrapped by a monitor use.
     *
     * <p> Заявлен как синхронизированный; вызов завершается использованием монитора.
     *
     * <p> Применимо к : method
     * @return is synchronized
     */
    public boolean isSynchronized(){ return has(ACC_SYNCHRONIZED); }

    /**
     * Declared synchronized; invocation is wrapped by a monitor use.
     *
     * <p> Заявлен как синхронизированный; вызов завершается использованием монитора.
     *
     * <p> Применимо к : method
     * @param v as synchronized
     * @return flags
     */
    public AccFlags withSynchronized(boolean v){ return new AccFlags(set(flags,ACC_SYNCHRONIZED,v)); }
    static { flagName.put("Syncronized", ACC_SYNCHRONIZED); }
    //endregion
    //region 0x00020 : transitive - Указывает, что любой модуль, который зависит от текущего модуля, неявно объявляет зависимость от модуля, указанного в этой записи. (module requires)
    /**
     * Indicates that any module which depends on the current module, implicitly declares a dependence on the module indicated by this entry.
     *
     * <p> Указывает, что любой модуль, который зависит от текущего модуля, неявно объявляет зависимость от модуля, указанного в этой записи.
     *
     * <p> Применимо к : module requires
     *
     * @return is transitive
     */
    public boolean isTransitive(){ return has(ACC_TRANSITIVE); }

    /**
     * Indicates that any module which depends on the current module, implicitly declares a dependence on the module indicated by this entry.
     *
     * <p> Указывает, что любой модуль, который зависит от текущего модуля, неявно объявляет зависимость от модуля, указанного в этой записи.
     *
     * <p> Применимо к : module requires
     *
     * @param v as transitive
     * @return flags
     */
    public AccFlags withTransitive(boolean v){ return new AccFlags(set(flags,ACC_TRANSITIVE,v)); }
    static { flagName.put("Transitive", ACC_TRANSITIVE); }
    //endregion
    //region 0x00040 : bridge - Метод моста, созданный компилятором. (method)
    /**
     * A bridge method, generated by the compiler.
     *
     * <p> Метод моста, созданный компилятором.
     *
     * <p> Применимо к : method
     *
     * <hr>
     *
     * https://www.baeldung.com/java-type-erasure
     *
     * <h3>4.1. Bridge Methods</h3>
     * To solve the edge case above, the compiler sometimes creates a bridge method.
     *
     * This is a synthetic method created by the Java compiler while compiling a class or interface that extends
     * a parameterized class or implements a parameterized interface where method signatures may be slightly different or ambiguous.
     *
     * <p>
     * In our example above, the Java compiler preserves polymorphism of generic types after erasure by ensuring
     * no method signature mismatch between IntegerStack‘s push(Integer) method and Stack‘s push(Object) method.
     *
     * <p>
     * Hence, the compiler creates a bridge method here:
     *
     * <pre>
     * public class IntegerStack extends Stack {
     *     // Bridge method generated by the compiler
     *
     *     public void push(Object value) {
     *         push((Integer)value);
     *     }
     *
     *     public void push(Integer value) {
     *         super.push(value);
     *     }
     * }
     * </pre>
     *
     * <p>
     * Consequently, Stack class's push method after type erasure, delegates to the original push method of IntegerStack class.
     *
     * @return is bridge
     */
    public boolean isBridge(){ return has(ACC_BRIDGE); }

    /**
     * A bridge method, generated by the compiler.
     *
     * <p> Метод моста, созданный компилятором.
     *
     * <p> Применимо к : method
     *
     * @param v as bridge
     * @return flags
     * @see #isBridge()
     */
    public AccFlags withBridge(boolean v){ return new AccFlags(set(flags,ACC_BRIDGE,v)); }
    static { flagName.put("Bridge", ACC_BRIDGE); }
    //endregion
    //region 0x00040 : staticPhase - Указывает, что эта зависимость обязательна в статической фазе, то есть во время компиляции, но необязательна в динамической фазе, то есть во время выполнения. (module requires)
    /**
     * Indicates that this dependence is mandatory in the static phase, i.e., at compile time, but is optional in the dynamic phase, i.e., at run time.
     *
     * <p> Указывает, что эта зависимость обязательна в статической фазе, то есть во время компиляции, но необязательна в динамической фазе, то есть во время выполнения.
     *
     * <p> Применимо к : module requires
     * @return is static phase
     */
    public boolean isStaticPhase(){ return has(ACC_STATIC_PHASE); }

    /**
     * Indicates that this dependence is mandatory in the static phase, i.e., at compile time, but is optional in the dynamic phase, i.e., at run time.
     *
     * <p> Указывает, что эта зависимость обязательна в статической фазе, то есть во время компиляции, но необязательна в динамической фазе, то есть во время выполнения.
     *
     * <p> Применимо к : module requires
     * @param v as static phase
     * @return flags
     */
    public AccFlags withStaticPhase(boolean v){ return new AccFlags(set(flags,ACC_STATIC_PHASE,v)); }
    static { flagName.put("StaticPhase", ACC_STATIC_PHASE); }
    //endregion
    //region 0x00040 : volatile - Объявлен летучим; не может быть кэширован. (field)
    /**
     * Declared volatile; cannot be cached.
     *
     * <p> Объявлен летучим; не может быть кэширован.
     *
     * <p> Применимо к : field
     *
     * @return is volatile
     */
    public boolean isVolatile(){ return has(ACC_VOLATILE); }

    /**
     * Declared volatile; cannot be cached.
     *
     * <p> Объявлен летучим; не может быть кэширован.
     *
     * <p> Применимо к : field
     *
     * @param v as volatile
     * @return flags
     */
    public AccFlags withVolatile(boolean v){ return new AccFlags(set(flags,ACC_VOLATILE,v)); }
    static { flagName.put("Volatile", ACC_VOLATILE); }
    //endregion
    //region 0x00080 : varArgs - Заявлен с переменным количеством аргументов. (method)
    /**
     * Declared with variable number of arguments.
     *
     * <p> Заявлен с переменным количеством аргументов.
     *
     * <p> Применимо к : method
     *
     * @return is var args
     */
    public boolean isVarArgs(){ return has(ACC_VARARGS); }

    /**
     * Declared with variable number of arguments.
     *
     * <p> Заявлен с переменным количеством аргументов.
     *
     * <p> Применимо к : method
     *
     * @param v as var args
     * @return flags
     */
    public AccFlags withVarArgs(boolean v){ return new AccFlags(set(flags,ACC_VARARGS,v)); }
    static { flagName.put("VarArgs", ACC_VARARGS); }
    //endregion
    //region 0x00080 : transient - Заявлен переходный; не записывается и не читается постоянным диспетчером объектов. (field)
    /**
     * Declared transient; not written or read by a persistent object manager.
     *
     * <p> Заявлен переходный; не записывается и не читается постоянным диспетчером объектов.
     *
     * <p> Применимо к : field
     *
     * @return is transient
     */
    public boolean isTransient(){ return has(ACC_TRANSIENT); }

    /**
     * Declared transient; not written or read by a persistent object manager.
     *
     * <p> Заявлен переходный; не записывается и не читается постоянным диспетчером объектов.
     *
     * <p> Применимо к : field
     * @param v as transient
     * @return flags
     */
    public AccFlags withTransient(boolean v){ return new AccFlags(set(flags,ACC_TRANSIENT,v)); }
    static { flagName.put("Transient", ACC_TRANSIENT); }
    //endregion
    //region 0x00100 : native - Заявлен родным; реализован на языке, отличном от языка программирования Java. (method)
    /**
     * Declared native; implemented in a language other than the Java programming language.
     *
     * <p> Заявлен родным; реализован на языке, отличном от языка программирования Java.
     *
     * <p> Применимо к : method
     *
     * @return is native
     */
    public boolean isNative(){ return has(ACC_NATIVE); }

    /**
     * Declared native; implemented in a language other than the Java programming language.
     *
     * <p> Заявлен родным; реализован на языке, отличном от языка программирования Java.
     *
     * <p> Применимо к : method
     *
     * @param v as native
     * @return flags
     */
    public AccFlags withNative(boolean v){ return new AccFlags(set(flags,ACC_NATIVE,v)); }
    static { flagName.put("Native", ACC_NATIVE); }
    //endregion
    //region 0x00200 : interface - Это интерфейс, а не класс. (class)
    /**
     * Is an interface, not a class.
     * <p>
     *     Это интерфейс, а не класс.
     *
     * <p> Применимо к : class
     * @return is interface
     */
    public boolean isInterface(){ return has(ACC_INTERFACE); }

    /**
     * set Is an interface, not a class.
     * <p>
     *     Это интерфейс, а не класс.
     *
     * <p> Применимо к : class
     * @param v as interface
     * @return flags
     */
    public AccFlags withInterface(boolean v){ return new AccFlags(set(flags,ACC_INTERFACE,v)); }
    static { flagName.put("Interface", ACC_INTERFACE); }
    //endregion
    //region 0x00400 : abstract  - Заявленная abstract; не должен быть создан (class, method)
    /**
     * Declared abstract; must not be instantiated.
     *
     * <p>
     * Заявленная abstract; не должен быть создан
     *
     * <p> Применимо к : class, method
     *
     * @return is abstract
     */
    public boolean isAbstract(){ return has(ACC_ABSTRACT); }

    /**
     * set abstract
     *
     * <p>
     * Заявленная abstract; не должен быть создан
     *
     * <p> Применимо к : class, method
     *
     * @param v Declared abstract; must not be instantiated.
     * @return flags
     */
    public AccFlags withAbstract(boolean v){ return new AccFlags(set(flags,ACC_ABSTRACT,v)); }
    static { flagName.put("Abstract", ACC_ABSTRACT); }
    //endregion
    //region 0x00800 : strict - Заявлен strictfp; режим с плавающей запятой является FP-строгим. (method)
    /**
     * Declared strictfp; floating-point mode is FP-strict.
     *
     * <p> Заявлен strictfp; режим с плавающей запятой является FP-строгим.
     *
     * <p> Применимо к : method
     *
     * @return is strict
     */
    public boolean isStrict(){ return has(ACC_STRICT); }

    /**
     * Declared strictfp; floating-point mode is FP-strict.
     *
     * <p> Заявлен strictfp; режим с плавающей запятой является FP-строгим.
     *
     * <p> Применимо к : method
     *
     * @param v as strict
     * @return flags
     */
    public AccFlags withStrict(boolean v){ return new AccFlags(set(flags,ACC_STRICT,v)); }
    static { flagName.put("Strict", ACC_STRICT); }
    //endregion
    //region 0x01000 : synthetic - Заявлен синтетический; отсутствует в исходном коде. (class, field, method, parameter, module *)
    /**
     * Declared synthetic; not present in the source code.
     *
     * <p> Заявлен синтетический; отсутствует в исходном коде.
     *
     * <p> Применимо к : class, field, method, parameter, module
     *
     * @return is synthetic
     */
    public boolean isSynthetic(){ return has(ACC_SYNTHETIC); }

    /**
     * set Declared synthetic; not present in the source code.
     *
     * <p> Заявлен синтетический; отсутствует в исходном коде.
     *
     * <p> Применимо к : class, field, method, parameter, module
     *
     * @param v as synthetic
     * @return flags
     */
    public AccFlags withSynthetic(boolean v){ return new AccFlags(set(flags,ACC_SYNTHETIC,v)); }
    static { flagName.put("Synthetic", ACC_SYNTHETIC); }
    //endregion
    //region 0x02000 : annotation - Объявлен как тип аннотации. (class)
    /**
     * Declared as an annotation type.
     * <p>
     *     Объявлен как тип аннотации.
     *
     * <p> Применимо к : class
     *
     * @return is annotation
     */
    public boolean isAnnotation(){ return has(ACC_ANNOTATION); }

    /**
     * set declared as an annotation type.
     * <p>
     *     Объявлен как тип аннотации.
     *
     * <p> Применимо к : class
     *
     * @param v Declared as an annotation type.
     * @return flags
     */
    public AccFlags withAnnotation(boolean v){ return new AccFlags(set(flags,ACC_ANNOTATION,v)); }
    static { flagName.put("Annotation", ACC_ANNOTATION); }
    //endregion
    //region 0x04000 : enum - Объявлен как перечисляемый тип. (class(?) field inner)
    /**
     * Declared as an enum type.
     * <p>
     * Объявлен как перечисляемый тип.
     *
     * <p> Применимо к : class(?), field inner
     * @return is enum
     */
    public boolean isEnum(){ return has(ACC_ENUM); }

    /**
     * Declared as an enum type.
     * <p>
     * Объявлен как перечисляемый тип.
     *
     * <p> Применимо к : class(?), field inner
     *
     * @param v as enum
     * @return flags
     */
    public AccFlags withEnum(boolean v){ return new AccFlags(set(flags,ACC_ENUM,v)); }
    static { flagName.put("Enum", ACC_ENUM); }
    //endregion
    //region 0x08000 : mandated (field, method, parameter, module, module *)
    /**
     * Indicates that the formal parameter was implicitly declared in source code,
     * according to the specification of the language in which the source code was written (JLS §13.1).
     * (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)
     *
     * <p>
     *
     * Указывает, что формальный параметр был неявно объявлен в исходном коде
     * в соответствии со спецификацией языка, на котором был написан исходный код (JLS §13.1).
     * (Формальный параметр требуется спецификацией языка, поэтому все компиляторы языка должны его выдавать.)
     *
     * <p> Применимо к : field, method, parameter, module, module
     *
     * @return is mandated
     */
    public boolean isMandated(){ return has(ACC_MANDATED); }

    /**
     * Indicates that the formal parameter was implicitly declared in source code,
     * according to the specification of the language in which the source code was written (JLS §13.1).
     * (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)
     *
     * <p>
     *
     * Указывает, что формальный параметр был неявно объявлен в исходном коде
     * в соответствии со спецификацией языка, на котором был написан исходный код (JLS §13.1).
     * (Формальный параметр требуется спецификацией языка, поэтому все компиляторы языка должны его выдавать.)
     *
     * <p> Применимо к : field, method, parameter, module, module
     *
     * @param v as mandated
     * @return flags
     */
    public AccFlags withMandated(boolean v){ return new AccFlags(set(flags,ACC_MANDATED,v)); }
    static { flagName.put("Mandated", ACC_MANDATED); }
    //endregion
    //region 0x08000 : module - Это модуль, а не класс или интерфейс. (class)
    /**
     * Is a module, not a class or interface.
     * <p>
     *     Это модуль, а не класс или интерфейс.
     *
     * <p> Применимо к : class
     * @return is module
     */
    public boolean isModule(){ return has(ACC_MODULE); }

    /**
     * set a module, not a class or interface.
     * <p>
     *     Это модуль, а не класс или интерфейс.
     *
     * <p> Применимо к : class
     * @param v as module
     * @return flags
     */
    public AccFlags withModule(boolean v){ return new AccFlags(set(flags,ACC_MODULE,v)); }
    static { flagName.put("Module", ACC_MODULE); }
    //endregion
    //region 0x10000 : record (class)
    public boolean isRecord(){ return has(ACC_RECORD); }
    public AccFlags withRecord(boolean v){ return new AccFlags(set(flags,ACC_RECORD,v)); }
    static { flagName.put("Record", ACC_RECORD); }
    //endregion
    //region 0x20000 : deprecated (class, field, method)
    public boolean isDeprecated(){ return has(ACC_DEPRECATED); }
    public AccFlags withDeprecated(boolean v){ return new AccFlags(set(flags,ACC_DEPRECATED,v)); }
    static { flagName.put("Deprecated", ACC_DEPRECATED); }
    //endregion

    static { flagName = Collections.unmodifiableMap(flagName); }

    public Set<String> flags(){
        LinkedHashSet<String> flags = new LinkedHashSet<>();
        for( var e : flagName.entrySet() ){
            if( has(e.getValue()) )flags.add(e.getKey());
        }
        return flags;
    }
    public AccFlags flags(Set<String> flags){
        if( flags==null )throw new IllegalArgumentException( "flags==null" );
        Map<String,Boolean> state = new HashMap<>();
        for( var f : flagName.keySet() ){
            state.put(f, flags.contains(f));
        }
        AccFlags res = this;
        for( var e : flagName.entrySet() ){
            var mask = e.getValue();
            var set = state.get(e.getKey());
            res = new AccFlags(set(res.value(),mask,set));
        }
        return res;
    }
}
