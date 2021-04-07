package xyz.cofe.trambda.bc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Содержит в себе описание+байт код метода/лямбды,
 * а так же связанных с ней других методов/лямбд {@link #getRefs()}
 */
public class MethodDef implements Serializable {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MethodDef(){
    }

    /**
     * Конструктор
     * @param access тип доступа к методу
     * @param name имя метода
     * @param descriptor типы аргументов
     * @param signature типы аргументов для generic
     * @param exceptions исключения
     */
    public MethodDef(int access, String name,String descriptor,String signature,String[] exceptions){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    //region byteCodes : List<ByteCode> - представление байт кода метода
    private List<ByteCode> byteCodes;

    /**
     * Возвращает представление байт кода метода
     * @return представление байт кода метода
     */
    public List<ByteCode> getByteCodes(){
        if( byteCodes==null )byteCodes = new ArrayList<>();
        return byteCodes;
    }

    /**
     * Указывает представление байт кода метода
     * @param byteCodes представление байт кода метода
     */
    public void setByteCodes(List<ByteCode> byteCodes){
        this.byteCodes = byteCodes;
    }
    //endregion
    //region access : int - тип доступа к методу (флаги)
    private int access;

    /**
     * Возвращает флаги метода
     * @return тип доступа к методу
     */
    public int getAccess(){
        return access;
    }

    /**
     * Указывает флаги метода
     * @param access тип доступа к методу
     */
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region flags : AccFlags - флаги метода

    /**
     * Возвращает флаги метода - это декодированное представление {@link #getAccess()}
     * @return флаги метода
     */
    public AccFlags getFlags(){
        return new AccFlags(access);
    }

    /**
     * Указывает флаги метода - это декодированное представление {@link #setAccess(int)} ()}
     * @param flags флаги метода
     */
    public void setFlags(AccFlags flags){
        if( flags==null )throw new IllegalArgumentException( "flags==null" );
        this.access = flags.value();
    }
    //endregion
    //region name : String - имя метода
    private String name;

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
    //region descriptor : String - типы аргументов и результата
    private String descriptor;

    /**
     * Возвращает типы аргументов и результата
     * @return типы аргументов и результата
     */
    public String getDescriptor(){
        return descriptor;
    }

    /**
     * Указывает типы аргументов и результата
     * @param descriptor типы аргументов и результата
     */
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region signature : String - типы аргументов и результата (generic)
    private String signature;

    /**
     * Возвращает типы аргументов и результата (generic)
     * @return типы аргументов и результата (generic); <b>возможно null</b>
     */
    public String getSignature(){
        return signature;
    }

    /**
     * Указывает типы аргументов и результата (generic)
     * @param signature типы аргументов и результата (generic); <b>возможно null</b>
     */
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region exceptions : String[] - генерируемые исключения - секция throws
    private String[] exceptions;

    /**
     * Возвращает генерируемые исключения - секция throws
     * @return генерируемые исключения - секция throws
     */
    public String[] getExceptions(){
        return exceptions;
    }

    /**
     * Указывает генерируемые исключения - секция throws
     * @param exceptions генерируемые исключения - секция throws
     */
    public void setExceptions(String[] exceptions){
        this.exceptions = exceptions;
    }
    //endregion

    //region refs : List<MethodDef>
    private List<MethodDef> refs;

    /**
     * Возвращает список связанных методов.
     *
     * <p>
     *     Связанные методы - это лямбды присутствующие в данном методе
     *
     * <br>
     * <code>
     *     apply( env0 -&gt; env0.getUsers().filter( <br>
     *     &nbsp;  <b>u -&gt; u.getName().contains("Petrov")</b> <br>
     *     ) )
     * </code>
     *
     * <br>
     * В данном примере <b>жирным</b> выделена связанная лямбда по отношению в внешней <code>env0 -&gt; ...</code>
     * @return список связанных методов
     */
    public synchronized List<MethodDef> getRefs(){
        if( refs==null ){
            refs = new ArrayList<>();
        }
        return refs;
    }

    /**
     * Указывает список связанных методов
     * @param refs список связанных методов
     * @see #getRefs()
     */
    public synchronized void setRefs(List<MethodDef> refs){ this.refs = refs; }
    //endregion
}
