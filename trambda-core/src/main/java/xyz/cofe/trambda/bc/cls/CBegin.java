package xyz.cofe.trambda.bc.cls;

import java.io.IOError;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.collection.ImTree;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.ClassDump;
import xyz.cofe.trambda.JavaClassName;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.AccFlagsProperty;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ClassFlags;

/**
 * Описывает класс / модуль
 */
public class CBegin implements ClsByteCode, ImTree<ByteCode>, ClazzWriter, AccFlagsProperty, ClassFlags {
    /**
     * Идентификатор версии при сериализации/де-сериализации
     */
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CBegin(){}

    /**
     * Конструктор
     * @param version версия байт-кода {@link #version}
     * @param access флаги доступа
     * @param name имя (байт-код) класса, см {@link JavaClassName}
     * @param signature сигнатура, в случае Generic типа
     * @param superName имя (байт-код) класса родителя
     * @param interfaces имена (байт-код) интерфейсов
     */
    public CBegin(int version, int access, String name, String signature, String superName, String[] interfaces){
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public CBegin(CBegin sample){
        if( sample==null )throw new IllegalArgumentException("sample==null");
        version = sample.getVersion();
        access = sample.getAccess();
        name = sample.getName();
        signature = sample.getSignature();
        superName = sample.getSuperName();
        if( sample.interfaces!=null )interfaces = Arrays.copyOf(sample.interfaces, sample.interfaces.length);

        source = sample.source!=null ? sample.source.clone() : null;
        outerClass = sample.outerClass!=null ? sample.outerClass.clone() : null;
        nestHost = sample.nestHost!=null ? sample.nestHost.clone() : null;
        permittedSubclass = sample.permittedSubclass!=null ? sample.permittedSubclass.clone() : null;

        if( sample.annotations!=null ){
            annotations = new ArrayList<>();
            for( var a : sample.annotations ){
                annotations.add( a!=null ? a.clone() : null );
            }
        }

        if( sample.typeAnnotations!=null ){
            typeAnnotations = new ArrayList<>();
            for( var a : sample.typeAnnotations ){
                typeAnnotations.add( a!=null ? a.clone() : null );
            }
        }

        if( sample.nestMembers!=null ){
            nestMembers = new ArrayList<>();
            for( var a : sample.nestMembers ){
                nestMembers.add( a!=null ? a.clone() : null );
            }
        }

        if( sample.innerClasses!=null ){
            innerClasses = new ArrayList<>();
            for( var a : sample.innerClasses ){
                innerClasses.add( a!=null ? a.clone() : null );
            }
        }

        if( sample.fields!=null ){
            fields = new ArrayList<>();
            for( var a : sample.fields ){
                fields.add( a!=null ? a.clone() : null );
            }
        }

        if( sample.methods!=null ){
            methods = new ArrayList<>();
            for( var a : sample.methods ){
                methods.add( a!=null ? a.clone() : null );
            }
        }

        if( sample.order!=null ){
            order = new LinkedHashMap<>();
            order.putAll(sample.order);
        }
    }

    /**
     * Создает полную копию класса
     * @return копия
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CBegin clone(){
        return new CBegin(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CBegin configure(Consumer<CBegin> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region version : int - версия байт-кода
    /** версия байт-кода
     * <ul>
     *     <li>45 - Java 1.0</li>
     *     <li>45.3 - Java 1.1</li>
     *     <li>46 - Java 1.2</li>
     *     <li>47 - Java 1.3</li>
     *     <li>48 - Java 1.4</li>
     *     <li>49 - Java 5</li>
     *     <li>50 - Java 6</li>
     *     <li>51 - Java 7</li>
     *     <li>52 - Java 8</li>
     *     <li>53 - Java 9</li>
     *     <li>54 - Java 10</li>
     *     <li>55 - Java 11</li>
     *     <li>56 - Java 12</li>
     *     <li>57 - Java 13</li>
     *     <li>58 - Java 14</li>
     *     <li>59 - Java 15</li>
     *     <li>60 - Java 16</li>
     *     <li>61 - Java 17</li>
     *     <li>62 - Java 18</li>
     * </ul>
     */
    protected int version;

    /**
     * Возвращает версию байт-кода совместимую с JVM {@link #version}
     * @return версия совместимая JVM
     */
    public int getVersion(){
        return version;
    }

    /**
     * Указывает версию байт-кода совместимую с JVM {@link #version}
     * @param version версия совместимая JVM
     */
    public void setVersion(int version){
        this.version = version;
    }
    //endregion
    //region access : int - флаги доступа к классу
    /**
     * флаги доступа к классу {@link AccFlags}
     */
    protected int access;

    /**
     * Возвращает флаги доступа к классу {@link AccFlags}
     * @return флаги доступа
     */
    public int getAccess(){
        return access;
    }

    /**
     * Указывает флаги доступа к классу {@link AccFlags}
     * @param access флаги доступа
     */
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name : String - имя класса
    protected String name;

    /**
     * Возвращает имя (байт-код) класса, см {@link #javaName()}, {@link JavaClassName}
     * @return имя класса
     */
    public String getName(){
        return name;
    }

    /**
     * Указывает имя (байт-код) класса, см {@link #javaName()}, {@link JavaClassName}
     * @param name имя класса
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Манипуляции с именем класса
     */
    public static class JavaNamed {
        public final CBegin cBegin;
        public JavaNamed(CBegin cBegin){
            if( cBegin==null )throw new IllegalArgumentException( "cBegin==null" );
            this.cBegin = cBegin;
        }

        //region name : String

        /**
         * Возвращает полное имя класса
         * @return полное имя класса, например <code>java.lang.String</code>
         */
        public String getName(){
            var n = cBegin.getName();
            if( n==null )return null;
            return new JavaClassName(n).name;
        }

        /**
         * Указывает полное имя класса
         * @param name полное имя класса, например <code>java.lang.String</code>
         */
        public void setName(String name){
            if( name==null ){
                cBegin.setName( null );
            }else {
                cBegin.setName( new JavaClassName(name).rawName() );
            }
        }
        //endregion
        //region simpleName : String - простое имя класса
        /**
         * Возвращает простое имя класса, например <code>String</code>
         * @return простое имя класса
         */
        public String getSimpleName(){
            var n = cBegin.getName();
            if( n==null )return null;

            return new JavaClassName(n).simpleName;
        }

        /**
         * Указывает простое имя класса, например <code>String</code>
         * @param name простое имя класса
         */
        public void setSimpleName( String name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            if( !JavaClassName.validId.matcher(name).matches() ){
                throw new IllegalArgumentException("name not match "+ JavaClassName.validId );
            }

            var curName = cBegin.getName();
            if( curName==null ){
                cBegin.setName(name);
                return;
            }

            cBegin.setName( new JavaClassName(curName).withSimpleName(name).rawName() );
        }
        //endregion
        //region package : String - имя пакета
        /**
         * Возвращает имя пакета содержащий класс, например <code>java.lang</code>
         * @return имя пакета
         */
        public String getPackage(){
            var n = cBegin.getName();
            if( n==null )return "";

            return new JavaClassName(n).packageName;
        }

        /**
         * Возвращает имя пакета содержащий класс, например <code>java.lang</code>
         * @param name имя пакета
         */
        public void setPackage(String name){
            if( name==null || name.length()<1 ){
                var n = cBegin.getName();
                if( n!=null ){
                    cBegin.setName( new JavaClassName(n).withPackage("").rawName() );
                }
            }else {
                var n = cBegin.getName();
                //noinspection ReplaceNullCheck
                if( n==null ){
                    cBegin.setName( new JavaClassName("Class0").withPackage(name).rawName() );
                }else{
                    cBegin.setName( new JavaClassName(n).withPackage(name).rawName() );
                }
            }
        }
        //endregion

        public String toString(){
            var n = cBegin.getName();
            if( n==null )return "null?";

            return new JavaClassName(n).name;
        }
    }

    /**
     * Изменение имени класса, меняет содержимое поля {@link #name}
     * @return управление именем класса
     */
    public JavaNamed javaName(){
        return new JavaNamed(this);
    }
    //endregion
    //region signature : String - сигнатура generic или null
    /**
     * Сигнатура в случае Generic класса/интерфейса или null
     */
    protected String signature;

    /**
     * Возвращает сигнатуру generic
     * @return сигнатура или null
     */
    public String getSignature(){
        return signature;
    }

    /**
     * Указывает сигнатуру generic
     * @param signature сигнатура или null
     */
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region superName : String - имя (байт-код) класса родителя
    /**
     * Имя (байт-код) класса родителя {@link JavaClassName}
     */
    protected String superName;

    /**
     * Возвращает имя (байт-код) класса родителя {@link JavaClassName}
     * @return имя (байт-код) класса родителя
     */
    public String getSuperName(){
        return superName;
    }

    /**
     * Указывает имя (байт-код) класса родителя {@link JavaClassName}
     * @param superName имя (байт-код) класса родителя
     */
    public void setSuperName(String superName){
        this.superName = superName;
    }
    //endregion
    //region interfaces : String[] - имена (байт-код) интерфейсов
    /**
     * имена (байт-код) интерфейсов, см {@link JavaClassName}
     */
    protected String[] interfaces;

    /**
     * Возвращает имена (байт-код) интерфейсов, см {@link JavaClassName}
     * @return имена (байт-код) интерфейсов
     */
    public String[] getInterfaces(){
        return interfaces;
    }

    /**
     * Указывает имена (байт-код) интерфейсов, см {@link JavaClassName}
     * @param interfaces имена (байт-код) интерфейсов
     */
    public void setInterfaces(String[] interfaces){
        this.interfaces = interfaces;
    }
    //endregion

    //region source : CSource - имя исходного файла
    /**
     * Содержит имя исходного класса/файла отладки (debug)
     */
    protected CSource source;

    /**
     * Возвращает имя исходного файла
     * @return имя исходного файла
     */
    public CSource getSource(){ return source; }

    /**
     * Указывает имя исходного файла
     * @param s имя исходного файла
     */
    public void setSource(CSource s){ source = s; }
    //endregion
    //region outerClass : COuterClass
    protected COuterClass outerClass;
    public COuterClass getOuterClass(){ return outerClass; }
    public void setOuterClass(COuterClass s){ outerClass = s; }
    //endregion
    //region nestHost : CNestHost
    protected CNestHost nestHost;
    public CNestHost getNestHost(){ return nestHost; }
    public void setNestHost(CNestHost s){ nestHost = s; }
    //endregion
    //region permittedSubclass : CPermittedSubclass
    protected CPermittedSubclass permittedSubclass;
    public CPermittedSubclass getPermittedSubclass(){ return permittedSubclass; }
    public void setPermittedSubclass(CPermittedSubclass s){ permittedSubclass = s; }
    //endregion

    // protected visitModule ...

    //region annotations : List<CAnnotation> - аннотации прикрепленные к классу
    /**
     * аннотации прикрепленные к классу
     */
    protected List<CAnnotation> annotations;

    /**
     * Возвращает аннотации прикрепленные к классу
     * @return аннотации
     */
    public List<CAnnotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }

    /**
     * Указывает аннотации прикрепленные к классу
     * @param ls аннотации
     */
    public void setAnnotations(List<CAnnotation> ls){
        annotations = ls;
    }
    //endregion
    //region typeAnnotations : List<CTypeAnnotation> - аннотации прикрепленные к классу
    /**
     * аннотации прикрепленные к классу
     */
    protected List<CTypeAnnotation> typeAnnotations;

    /**
     * Возвращает аннотации прикрепленные к классу
     * @return аннотации
     */
    public List<CTypeAnnotation> getTypeAnnotations(){
        if( typeAnnotations==null )typeAnnotations = new ArrayList<>();
        return typeAnnotations;
    }

    /**
     * Указывает аннотации прикрепленные к классу
     * @param ls аннотации
     */
    public void setTypeAnnotations(List<CTypeAnnotation> ls){
        typeAnnotations = ls;
    }
    //endregion

    // protected List visitAttribute

    //region nestMembers : List<CNestMember>
    protected List<CNestMember> nestMembers;
    public List<CNestMember> getNestMembers(){
        if( nestMembers==null )nestMembers = new ArrayList<>();
        return nestMembers;
    }
    public void setNestMembers(List<CNestMember> ls){
        nestMembers = ls;
    }
    //endregion
    //region innerClasses : List<CInnerClass>
    protected List<CInnerClass> innerClasses;
    public List<CInnerClass> getInnerClasses(){
        if( innerClasses==null )innerClasses = new ArrayList<>();
        return innerClasses;
    }
    public void setInnerClasses(List<CInnerClass> ls){
        innerClasses = ls;
    }
    //endregion

    // protected List visitRecordComponent

    //region fields : List<CField> - Список полней класса
    /**
     * Список полней класса
     */
    protected List<CField> fields;

    /**
     * Возвращает список полней класса
     * @return список полней класса
     */
    public List<CField> getFields(){
        if( fields==null )fields = new ArrayList<>();
        return fields;
    }

    /**
     * Указывает список полней класса
     * @param fields список полней класса
     */
    public void setFields(List<CField> fields){
        this.fields = fields;
    }
    //endregion
    //region methods : List<CMethod> - список методов класса
    /**
     * Список методов класса
     */
    protected List<CMethod> methods;

    /**
     * Возвращает список методов класса
     * @return список методов класса
     */
    public List<CMethod> getMethods(){
        if( methods==null )methods = new ArrayList<>();
        return methods;
    }

    /**
     * Указывает список методов класса
     * @param methods список методов класса
     */
    public void setMethods(List<CMethod> methods){
        this.methods = methods;
    }
    //endregion
    //region order : Map<ClsByteCode,Integer>
    /**
     * Содержит порядок определения полей/методов/.. в классе (байт-коде)
     */
    protected Map<ClsByteCode,Integer> order;

    /**
     * Возвращает порядок определения полей/методов/.. в классе (байт-коде)
     * @return порядок определения полей/методов/..
     */
    public Map<ClsByteCode,Integer> getOrder(){
        if( order==null )order = new LinkedHashMap<>();
        return order;
    }

    /**
     * Указывает  порядок определения полей/методов/.. в классе (байт-коде)
     * @param order порядок определения полей/методов/..
     */
    public void setOrder(Map<ClsByteCode,Integer> order){
        this.order = order;
    }

    /**
     * Указывает порядок определения полей/методов/..
     * @param c поле/методо/...
     * @param order порядок
     * @return SELF ссылка
     */
    public CBegin order(ClsByteCode c, int order){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        getOrder().put(c,order);
        return this;
    }
    //endregion

    @Override
    public String toString(){
        return CBegin.class.getSimpleName()+" " +
            "version=" + version +
            " access="+access+("#"+new AccFlags(access).flags())+
            " name=" + name +
            " signature=" + signature +
            " superName=" + superName +
            " interfaces=" + Arrays.toString(interfaces) +
            "";
    }

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @SuppressWarnings("unchecked")
    @Override
    public Eterable<ByteCode> nodes(){
        Eterable<ByteCode> e = Eterable.of(
            source, outerClass, nestHost, permittedSubclass
        );
        if( annotations!=null && !annotations.isEmpty() ) e = e.union( Eterable.of( annotations ) );
        if( typeAnnotations!=null && !typeAnnotations.isEmpty() ) e = e.union( Eterable.of( typeAnnotations ) );
        if( nestMembers!=null && !nestMembers.isEmpty() ) e = e.union( Eterable.of( nestMembers ) );
        if( innerClasses!=null && !innerClasses.isEmpty() ) e = e.union( Eterable.of( innerClasses ) );
        if( fields!=null && !fields.isEmpty() ) e = e.union( Eterable.of( fields ) );
        if( methods!=null && !methods.isEmpty() ) e = e.union( Eterable.of( methods ) );

        return e.notNull();
    }

    //region toByteCode(), parseByteCode()

    /**
     * Генерация байт кода
     * @param v куда будет записан байт код
     */
    @Override
    public void write( ClassWriter v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        v.visit( getVersion(), getAccess(), getName(), getSignature(), getSuperName(), getInterfaces() );

        var src = source;
        if( src!=null )src.write(v);

        var nh = nestHost;
        if( nh!=null )nh.write(v);

        var pss = permittedSubclass;
        if( pss!=null )pss.write(v);

        var oc = outerClass;
        if( oc!=null )oc.write(v);

        List<ClsByteCode> anns = new ArrayList<>();
        if( annotations!=null )anns.addAll(annotations);
        if( typeAnnotations!=null )anns.addAll(typeAnnotations);
        anns.sort( (a,b)->{
            int o1 = getOrder().getOrDefault(a,-1);
            int o2 = getOrder().getOrDefault(b,-1);
            if( o1==o2 )return a.toString().compareTo(b.toString());
            return Integer.compare(o1,o2);
        });

        for( var ann : anns ){
            ann.write(v);
        }

        List<ClsByteCode> body = new ArrayList<>();
        if( fields!=null )body.addAll(fields);
        if( methods!=null )body.addAll(methods);
        if( nestMembers!=null )body.addAll(nestMembers);
        if( innerClasses!=null )body.addAll(innerClasses);

        body.sort( (a,b)->{
            int o1 = getOrder().getOrDefault(a,-1);
            int o2 = getOrder().getOrDefault(b,-1);
            if( o1==o2 )return a.toString().compareTo(b.toString());
            return Integer.compare(o1,o2);
        });

        for( var b : body ){
            b.write(v);
        }

        v.visitEnd();
    }

    /**
     * Возвращает байт-код.
     * <br>
     * Класс будет сгенерирован с использованием таких флагов
     * <code>
     * new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES)
     * </code>
     * @return байт-код
     */
    public byte[] toByteCode(){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        write(cw);
        return cw.toByteArray();
    }

    /**
     * Парсинг байт-кода
     * @param byteCode байт-код
     * @return представление класса
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static CBegin parseByteCode(byte[] byteCode){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );

        ClassReader classReader = new ClassReader(byteCode);
        List<ByteCode> byteCodes = new ArrayList<>();

        ClassDump dump = new ClassDump();
        dump.byteCode( byteCodes::add );
        classReader.accept(dump,0);

        return byteCodes.stream().filter( b -> b instanceof CBegin )
            .map( b -> (CBegin)b ).findFirst().get();
    }

    /**
     * Парсинг байт-кода класса.
     * <ul>
     *     <li>Будет произведен поиск класса в ресурсах
     *
     *     <br>
     *     <code>var resName = "/"+clazz.getName().replace(".","/")+".class"</code>
     *
     *     <br>
     *     <code>
     *          clazz.getResource(resName)
     *     </code>
     *     </li>
     *     <li>Если ресурс будет найдет, то быдет вызов {@link #parseByteCode(URL)}</li>
     * </ul>
     * @param clazz класс
     * @return представление байт кода
     */
    public static CBegin parseByteCode(Class<?> clazz){
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );

        var resName = "/"+clazz.getName().replace(".","/")+".class";
        var classUrl = clazz.getResource(resName);

        if( classUrl==null )throw new IOError(
            new IOException("resource "+resName+" not found")
        );

        return parseByteCode(classUrl);
    }

    /**
     * Парсинг байт-кода
     * @param url ссылка на байт-код
     * @return представление класса
     */
    public static CBegin parseByteCode(URL url){
        if( url==null )throw new IllegalArgumentException( "url==null" );
        try{
            return parseByteCode(IOFun.readBytes(url));
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }
    //endregion
}
