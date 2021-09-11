package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;

/**
 * This opcode is either
 * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
 * <hr>
 * <a href="https://coderoad.ru/13764238/%D0%97%D0%B0%D1%87%D0%B5%D0%BC-%D0%BD%D1%83%D0%B6%D0%B5%D0%BD-invokeSpecial-%D0%BA%D0%BE%D0%B3%D0%B4%D0%B0-%D1%81%D1%83%D1%89%D0%B5%D1%81%D1%82%D0%B2%D1%83%D0%B5%D1%82-invokeVirtual">Зачем нужен invokeSpecial, когда существует invokeVirtual</a>
 * <br> Ответ можно легко найти, если внимательно прочитать спецификацию Java VM:
 * Разница между инструкциями invokespecial и invokevirtual заключается в том, что invokevirtual вызывает метод, основанный на классе объекта. Инструкция invokespecial используется для вызова методов инициализации экземпляра, а также частных методов и методов суперкласса текущего класса.
 * Другими словами, invokespecial используется для вызова методов, не заботясь о динамической привязке, чтобы вызвать версию метода конкретного класса.
 * 
 * <hr>
 * <a href="">В чем смысл invokeinterface?</a> <br>
 * Каждый класс Java связан с таблицей виртуальных методов , содержащей "links" байт-кода каждого метода класса. 
 * Эта таблица наследуется от суперкласса определенного класса и расширяется в отношении новых методов подкласса. E.g.,
<pre>
class BaseClass {
    public void method1() { }
    public void method2() { }
    public void method3() { }
}

class NextClass extends BaseClass {
    public void method2() { } // overridden from BaseClass
    public void method4() { }
}
</pre>
* результаты в таблицах
<pre>
BaseClass
1. BaseClass/method1()
2. BaseClass/method2()
3. BaseClass/method3()

NextClass
1. BaseClass/method1()
2. NextClass/method2()
3. BaseClass/method3()
4. NextClass/method4()
</pre>
* Обратите внимание, как таблица виртуальных методов NextClass сохраняет порядок записей таблицы BaseClass и просто перезаписывает "link" из method2() , который она переопределяет.
* <p> Таким образом, реализация JVM может оптимизировать вызов invokevirtual , помня, что BaseClass/method3() всегда будет третьей записью в таблице виртуальных методов любого объекта, на котором когда-либо будет вызван этот метод.
* <p> С invokeinterface такая оптимизация невозможна. E.g.,
<pre>
interface MyInterface {
    void ifaceMethod();
}

class AnotherClass extends NextClass implements MyInterface {
    public void method4() { } // overridden from NextClass
    public void ifaceMethod() { }
}

class MyClass implements MyInterface {
    public void method5() { }
    public void ifaceMethod() { }
}
</pre>
* Эта иерархия классов приводит к таблицам виртуальных методов
<pre>
AnotherClass
1. BaseClass/method1()
2. NextClass/method2()
3. BaseClass/method3()
4. AnotherClass/method4()
5. MyInterface/ifaceMethod()

MyClass
1. MyClass/method5()
2. MyInterface/ifaceMethod()
</pre>
* Как вы можете видеть, AnotherClass содержит метод интерфейса в его пятой записи, 
* а MyClass содержит его во второй записи. Чтобы на самом деле найти правильную запись 
* в таблице виртуальных методов, вызов метода с invokeinterface всегда должен будет 
* искать полную таблицу, не имея возможности для стиля оптимизации, который делает invokevirtual.
* 
* <p> Существуют дополнительные различия, такие как тот факт, что invokeinterface 
* может использоваться вместе со ссылками на объекты, которые на самом деле не 
* реализуют интерфейс. Поэтому invokeinterface должен будет проверить во время 
* выполнения, существует ли метод в таблице, и потенциально вызвать исключение.
 */
public class MMethodInsn extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MMethodInsn(){}
    
    /**
     * Конструктор
     * @param op the opcode of the type instruction to be visited. This opcode is either
     *                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     * <br>
     * код операции типа инструкции, которую нужно посетить. Этот код операции - INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC или INVOKEINTERFACE.
     * 
     * @param owner the internal name of the method's owner class (see {@link
     *                    org.objectweb.asm.Type#getInternalName()}).
     * <br>
     * внутреннее имя класса владельца метода (см. {@link org.objectweb.asm.Type # getInternalName ()}).
     * 
     * @param name the method's name.
     * <br> название метода.
     * 
     * @param descriptor the method's descriptor (see {@link org.objectweb.asm.Type}).
     * <br> дескриптор метода (см. {@link org.objectweb.asm.Type}).
     * 
     * @param iface if the method's owner class is an interface.
     * <br> если класс владельца метода является интерфейсом.
     */
    public MMethodInsn(int op, String owner, String name, String descriptor, boolean iface){
        this.opcode = op;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.iface = iface;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MMethodInsn(MMethodInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        opcode = sample.opcode;
        owner = sample.owner;
        name = sample.name;
        descriptor = sample.descriptor;
        iface = sample.iface;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MMethodInsn clone(){ return new MMethodInsn(this); }

    //region opcode : int
    private int opcode;
    public int getOpcode(){
        return opcode;
    }
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region owner : String
    private String owner;
    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
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
    //region iface : boolean
    private boolean iface;

    public boolean isIface(){
        return iface;
    }

    public void setIface(boolean iface){
        this.iface = iface;
    }
    //endregion

    public String toString(){
        return MMethodInsn.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+"" +
            " owner="+owner+" name="+name+" desc="+descriptor+" iface="+iface
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitMethodInsn(getOpcode(),getOwner(),getName(),getDescriptor(),isIface());
    }
}
