package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.AccFlagsProperty;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.MethodFlags;
import xyz.cofe.trambda.bc.mth.MEnd;
import xyz.cofe.trambda.bc.mth.MethodByteCode;
import xyz.cofe.trambda.bc.mth.MethodWriterCtx;

/**
 * Описывает метод класса
 */
public class CMethod implements ClsByteCode, ClazzWriter, AccFlagsProperty, MethodFlags {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CMethod(){}

    /**
     * Конструктор
     * @param access флаги доступа к методу {@link AccFlags}
     * @param name имя метода
     * @param descriptor дескриптор типов параметров и результата
     * @param signature сигнатура generic параметров и результата
     * @param exceptions исключения генерируемые методом
     */
    public CMethod(int access, String name, String descriptor, String signature, String[] exceptions){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CMethod(CMethod sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        access = sample.getAccess();
        name = sample.getName();
        descriptor = sample.getDescriptor();
        signature = sample.getSignature();
        exceptions = sample.getExceptions();
        if( sample.methodByteCodes!=null ){
            methodByteCodes = new ArrayList<>();
            for( var mb : sample.methodByteCodes ){
                methodByteCodes.add( mb!=null ? mb.clone() : null );
            }
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CMethod clone(){
        return new CMethod(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CMethod configure(Consumer<CMethod> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region access : int - флаги доступа к методу
    /**
     * флаги доступа к методу {@link AccFlags}
     */
    protected int access;

    /**
     * Возвращает флаги доступа к методу {@link AccFlags}
     * @return флаги доступа
     */
    public int getAccess(){
        return access;
    }

    /**
     * Указывает флаги доступа к методу {@link AccFlags}
     * @param access флаги доступа
     */
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name : String - имя метода
    /**
     * имя метода
     */
    protected String name;

    /**
     * Возвращает имя метода
     * @return имя метода
     */
    public String getName(){
        return name;
    }

    /**
     * Указывает имя метода
     * @param name имя метода
     */
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String - дескриптор типов параметров и результата
    /**
     * дескриптор типов параметров и результата
     */
    protected String descriptor;

    /**
     * Возвращает дескриптор типов параметров и результата
     * @return дескриптор типов параметров и результата
     */
    public String getDescriptor(){
        return descriptor;
    }

    /**
     * Указывает дескриптор типов параметров и результата
     * @param descriptor дескриптор типов параметров и результата
     */
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region signature : String - сигнатура generic параметров и результата
    /**
     * сигнатура generic параметров и результата
     */
    protected String signature;

    /**
     * Возвращает сигнатуру generic параметров и результата
     * @return сигнатура generic параметров и результата
     */
    public String getSignature(){
        return signature;
    }

    /**
     * Указывает сигнатуру generic параметров и результата
     * @param signature сигнатура generic параметров и результата
     */
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region exceptions : String[] - исключения генерируемые методом
    /**
     * исключения генерируемые методом
     */
    protected String[] exceptions;

    /**
     * Возвращает исключения генерируемые методом
     * @return исключения
     */
    public String[] getExceptions(){
        return exceptions;
    }

    /**
     * Указывает исключения генерируемые методом
     * @param exceptions исключения
     */
    public void setExceptions(String[] exceptions){
        this.exceptions = exceptions;
    }
    //endregion
    //region methodByteCodes : List<MethodByteCode> - байт-код метода
    /**
     * байт-код метода
     */
    protected List<MethodByteCode> methodByteCodes;

    /**
     * Возвращает байт-код метода
     * @return байт-код метода
     */
    public List<MethodByteCode> getMethodByteCodes(){
        if( methodByteCodes==null )methodByteCodes = new ArrayList<>();
        return methodByteCodes;
    }

    /**
     * Указывает байт-код метода
     * @param ls байт-код метода
     */
    public void setMethodByteCodes(List<MethodByteCode> ls){
        methodByteCodes = ls;
    }
    //endregion

    @Override
    public String toString(){
        return "CMethod " +
            "access="+access+("#"+new AccFlags(access).flags())+
            " name="+name +
            " descriptor=" + descriptor +
            " signature=" + signature +
            " exceptions=" + Arrays.toString(exceptions);
    }

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public Eterable<ByteCode> nodes(){
        if( methodByteCodes!=null )return Eterable.of(methodByteCodes);
        return Eterable.empty();
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var mv = v.visitMethod(
            getAccess(),getName(),getDescriptor(),getSignature(),getExceptions()
            );

        var ctx = new MethodWriterCtx();

        var body = methodByteCodes;
        if( body!=null ){
            for( var b : body ){
                if( b!=null && !(b instanceof MEnd) ){
                    b.write(mv, ctx);
                }
            }
        }

        mv.visitEnd();
    }
}
