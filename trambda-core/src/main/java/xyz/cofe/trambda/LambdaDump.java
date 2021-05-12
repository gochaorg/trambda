package xyz.cofe.trambda;

import java.io.IOError;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Tuple;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.fn.Tuple4;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.mth.MInvokeDynamicInsn;

/**
 * Дамп лямбды
 */
public class LambdaDump implements Serializable {
    public LambdaDump(){
    }
    public LambdaDump configure(Consumer<LambdaDump> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region capturedArgs : List<Object> - захваченные параметры
    protected List<Object> capturedArgs;
    public List<Object> getCapturedArgs(){
        if( capturedArgs==null ){
            capturedArgs = new ArrayList<>();
        }
        return capturedArgs;
    }
    public void setCapturedArgs(List<Object> ls){
        if( ls==null )throw new IllegalArgumentException( "ls==null" );
        capturedArgs = ls;
    }
    //endregion
    //region lambdaNode : LambdaNode
    protected LambdaNode lambdaNode;
    public LambdaNode getLambdaNode(){
        return lambdaNode;
    }
    public void setLambdaNode(LambdaNode node){
        this.lambdaNode = node;
    }
    //endregion
    //region dump() - создание дампа
    /**
     * Создание дампа лямбды
     * @param fn лямбда
     * @return дамп
     */
    public synchronized LambdaDump dump( Fn1<?,?> fn ){
        if( fn==null )throw new IllegalArgumentException( "fn==null" );
        return  dumpSLambda((Serializable)fn);
    }

    /**
     * Очистка кэша
     */
    public synchronized void invalidateCache(){
        serializableLambdaCache.clear();
        classByteCodeCache.clear();
        lambdaNodeCache.clear();
    }

    /**
     * кеш лямбда / сериализованная форма
     */
    public transient final Map<Serializable,SerializedLambda> serializableLambdaCache
        = new LinkedHashMap<>();

    /**
     * кеш сериализованная лямбда / корневой узел байт кода
     */
    public transient final Map<SerializedLambda,LambdaNode> lambdaNodeCache
        = new LinkedHashMap<>();

    protected void onSerializedLambda( SerializedLambda sl ){
    }

    protected void onLambdaNode( LambdaNode ln ){
    }

    protected boolean cacheSerializedLambda = true;
    public synchronized boolean isCacheSerializedLambda(){
        return cacheSerializedLambda;
    }
    public synchronized void setCacheSerializedLambda( boolean v ){
        cacheSerializedLambda = v;
    }

    /**
     * создание дампа лямбды
     * @param serializableLambda лямбда
     * @return дамп
     */
    protected synchronized LambdaDump dumpSLambda(Serializable serializableLambda ){
        if( serializableLambda==null )throw new IllegalArgumentException( "serializableLambda==null" );

        SerializedLambda sl;
        try{
            if( cacheSerializedLambda ){
                sl = serializableLambdaCache.computeIfAbsent(
                    serializableLambda, x -> serializedLambda(serializableLambda));
            }else{
                sl = serializedLambda(serializableLambda);
            }
        } catch( Exception e ) {
            throw new IOError(e);
        }

        onSerializedLambda(sl);

        var lnode = lambdaNodeCache.computeIfAbsent(
            sl,
            x -> {
                refs.clear();
                return lambdaNode(
                    serializableLambda.getClass(),
                    sl.getImplClass(),
                    sl.getImplMethodName(),
                    sl.getImplMethodSignature()
                );
            }
        );

        onLambdaNode(lnode);

        var ldump = new LambdaDump();
        ldump.setLambdaNode(lnode);

        if( sl.getCapturedArgCount()>0 ){
            for( var i=0; i<sl.getCapturedArgCount();i++ ){
                ldump.getCapturedArgs().add(sl.getCapturedArg(i));
            }
        }

        return ldump;
    }

    /**
     * получение сералиазованной лямбды
     * @param lambda лямбда
     * @return сериализованная лямбда
     */
    protected SerializedLambda serializedLambda(Serializable lambda) {
        if( lambda==null )throw new IllegalArgumentException( "lambda==null" );
        Method method = null;
        try{
            method = lambda.getClass().getDeclaredMethod("writeReplace");
        } catch( NoSuchMethodException e ) {
            throw new Error(e);
        }
        method.setAccessible(true);
        try{
            return (SerializedLambda) method.invoke(lambda);
        } catch( IllegalAccessException | InvocationTargetException e ) {
            throw new Error(e);
        }
    }

    /**
     * кеш байт кода
     */
    public transient final Map<Class<?>,Map<String,CBegin>> classByteCodeCache
        = new HashMap<>();

    /**
     * получение байт кода класса
     * @param baseClass класс для доступа к ресурсам (*.class)
     * @param clazz целевой класс, чей байт код интересует
     * @return байт код
     * @throws IOException ошибка чтение байт кода
     */
    protected CBegin classByteCode(  Class<?> baseClass, String clazz ) throws IOException {
        if( baseClass==null )throw new IllegalArgumentException( "baseClass==null" );
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );

        var cachedMap = classByteCodeCache.computeIfAbsent(baseClass, b -> new HashMap<>());
        var cachedValue = cachedMap.get(clazz);
        if( cachedValue!=null )return cachedValue;

        var lambdaClassURL = baseClass.getResource(
            "/"+clazz.replace(".","/")+".class"
        );
        if( lambdaClassURL==null ){
            throw new IOException("bytecode of class "+clazz+" not found");
        }

        var cbegin = CBegin.parseByteCode(lambdaClassURL);
        cachedMap.put(clazz, cbegin);

        return cbegin;
    }

    protected transient final Map<MHandle, LambdaNode> refs = new LinkedHashMap<>();

    /**
     * Создание узла для байт кода лямбды и связанных лямбд
     * @param baseClass класс для доступа к ресурсам (*.class)
     * @param implClass класс содержащий код лямбды
     * @param methName имя метода класса содержащий код лямбды
     * @param methSign сигнатура метода класса содержащий код лямбды
     * @return узел для лямбды и связанных лямбд
     */
    protected synchronized LambdaNode lambdaNode( Class<?> baseClass, String implClass, String methName, String methSign )
    {
        if( baseClass==null )throw new IllegalArgumentException( "baseClass==null" );
        if( implClass==null )throw new IllegalArgumentException( "implClass==null" );
        if( methName==null )throw new IllegalArgumentException( "methName==null" );
        if( methSign==null )throw new IllegalArgumentException( "methSign==null" );

        CBegin classByteCode = null;
        try{
            classByteCode = classByteCode(baseClass, implClass);
        } catch( IOException e ) {
            throw new IOError(e);
        }

        var node = new LambdaNode();
        node.setClazz(classByteCode);

        var cmethod =
        classByteCode
            .getMethods()
            .stream()
            .filter( m -> methName.equals(m.getName()) && methSign.equals(m.getDescriptor()) )
            .findFirst();

        cmethod.ifPresent( m -> {
            node.setMethod(m);

            m.getMethodByteCodes()
                .stream()
                .map( bc -> bc instanceof MInvokeDynamicInsn ? (MInvokeDynamicInsn)bc : null )
                .filter(Objects::nonNull)
                .flatMap(idi ->
                    idi.getBootstrapMethodArguments().stream().map( arg -> {
                        if( !(arg instanceof HandleArg) ){
                            return null;
                        }
                        var ha = (HandleArg)arg;
                        var h = ha.getHandle();
                        if( h==null )return null;
                        if( h.getName().startsWith("lambda$") && h.getTag()== Opcodes.H_INVOKESTATIC ){
                            return h;
                        }
                        return null;
                    })
                )
                .filter( Objects::nonNull )
                .map( h -> {
                    var rrefs = refs.get(h);
                    if( rrefs!=null ){
                        return rrefs;
                    }
                    var mdefChild = lambdaNode(baseClass, h.getOwner(), h.getName(), h.getDesc());
                    if( mdefChild!=null ){
                        refs.put(h,mdefChild);
                    }
                    return mdefChild;
                }).forEach( ref -> {
                    node.getNodes().add(ref);
                });
        });

        return node;
    }
    //endregion

    /**
     * Восстановление класса
     */
    public static class Restore {
        private static final Logger rlog = LoggerFactory.getLogger(Restore.class);
        protected final LambdaDump dump;

        public Restore(LambdaDump dump){
            if( dump==null )throw new IllegalArgumentException( "dump==null" );
            this.dump = dump;
        }

        //region className
        protected static final AtomicInteger classNameIdSeq = new AtomicInteger(0);
        protected JavaClassName className = new JavaClassName(
            LambdaDump.class.getPackageName()+
                "."+LambdaDump.class.getSimpleName().toLowerCase()+
                ".AutoGen"+classNameIdSeq.getAndIncrement());

        /**
         * Возвращает имя генерируемого класса
         * @return имя генерируемого класса
         */
        public synchronized JavaClassName className(){
            return className;
        }

        /**
         * Указывает имя генерируемого класса
         * @param name имя генерируемого класса
         * @return SELF ссылка
         */
        public synchronized Restore className(String name){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            className = new JavaClassName(name);
            return this;
        }

        /**
         * Указывает имя генерируемого класса
         * @param name имя генерируемого класса
         * @return SELF ссылка
         */
        public synchronized Restore className(JavaClassName name){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            className = name;
            return this;
        }
        //endregion
        //region rootMethodName : String
        protected String rootMethodName = "_root_";

        /**
         * Возвращает имя метода который содержит реализацию лямбды
         * @return имя root метода
         */
        public synchronized String rootMethodName(){
            return rootMethodName;
        }

        /**
         * Указывает имя метода который содержит реализацию лямбды
         * @param name имя root метода
         * @return SELF ссылка
         */
        public synchronized Restore rootMethodName(String name){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            if( !JavaClassName.validId.matcher(name).matches() )
                throw new IllegalArgumentException("name not matched "+JavaClassName.validId);

            rootMethodName = name;
            return this;
        }
        //endregion
        //region generateClassBytecode()
        /**
         * Генерирует класс
         * @return класс
         */
        public synchronized CBegin classByteCode(){
            return classByteCode(null);
        }

        /**
         * Генерирует класс
         * @param rootConsumer (возможно null) получает класс/метод
         * @return класс
         */
        public synchronized CBegin classByteCode(Consumer<Tuple2<CBegin,CMethod>> rootConsumer){
            var srcRootNode = dump.getLambdaNode();
            if( srcRootNode==null )throw new IllegalStateException("root lambda node is null");

            var srcRootClass = srcRootNode.getClazz();
            if( srcRootClass==null )throw new IllegalStateException("root class (CBegin) is null");

            var srcRootMethod = srcRootNode.getMethod();
            if( srcRootMethod==null )throw new IllegalStateException("root method (CMethod) is null");

            CBegin rootClass = new CBegin();
            rootClass.javaName().setName(className.name);
            rootClass.setAccess(33);
            rootClass.setVersion(srcRootClass.getVersion());
            rootClass.setSuperName(Object.class.getName().replace(".","/"));
            rootClass.setInterfaces(new String[]{});
            rlog.debug("generate class {}",rootClass);

            var rootMethod = srcRootMethod.clone();
            rootMethod.setName("_root_");
            rootMethod.setPrivate(false);
            rootMethod.setPublic(true);
            rootClass.getMethods().add( rootMethod );
            rlog.debug("add method {}",rootMethod);

            var methods = new LinkedHashMap<LambdaNode, CMethod>();
            methods.put(srcRootNode, rootMethod);

            var relinkMap = new HashMap<CMethod,List<Tuple4<CBegin,CMethod,CBegin,CMethod>>>();

            if( rlog.isTraceEnabled() ){
                srcRootNode.walk().tree().forEach( t -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("..".repeat(t.getLevel()));

                    var n = t.getNode();
                    if( n.getClazz()!=null )sb.append(" class=").append(n.getClazz().javaName());

                    var m = n.getMethod();
                    if( m!=null )sb.append(" method=").append(m.getName()).append(" ").append(m.getDescriptor());

                    rlog.trace(sb.toString());
                });
            }

            srcRootNode.walk().tree().forEach( ts -> {
                if( ts.getLevel()<=0 )return;

                var parent = ts.getParent().getNode();
                var node = ts.getNode();
                var nodeClazz = node.getClazz();
                var nodeMethod = node.getMethod();

                var nm = nodeMethod.clone();
                methods.put(node,nm);
                rootClass.getMethods().add(nm);

                var prntMethod = methods.get(parent);
                relinkMap.computeIfAbsent( prntMethod, x ->
                    new ArrayList<>()).add( Tuple.of(nodeClazz,nodeMethod,rootClass,nm)
                );
            });

            for( var smeth : relinkMap.keySet() ){
                smeth.getMethodByteCodes().stream().map(
                    b -> b instanceof MInvokeDynamicInsn ?
                        (MInvokeDynamicInsn)b : null
                ).filter( Objects::nonNull )
                    .forEach( invd -> {
                        invd.getBootstrapMethodArguments()
                            .stream()
                            .filter( ma -> ma instanceof HandleArg)
                            .map( ma -> (HandleArg)ma )
                            .forEach( ha -> {
                                for( var tlink : relinkMap.get(smeth) ){
                                    var mh = ha.getHandle();
                                    if( mh!=null ){
                                        if( mh.getOwner().equals(tlink.a().getName()) &&
                                            mh.getName().equals(tlink.b().getName()) &&
                                            mh.getDesc().equals(tlink.b().getDescriptor())
                                        ){
                                            var nmh = new MHandle();
                                            nmh.setName(tlink.d().getName());
                                            nmh.setOwner(tlink.c().getName());
                                            nmh.setDesc(tlink.d().getDescriptor());
                                            nmh.setTag(mh.getTag());
                                            nmh.setIface(mh.isIface());

                                            ha.setHandle(nmh);

                                            rlog.debug(
                                                "linked {} -> {}",
                                                tlink.a().javaName()+" "+tlink.b().getName()+tlink.b().getDescriptor(),
                                                tlink.c().javaName()+" "+tlink.d().getName()+tlink.d().getDescriptor()
                                            );
                                        }
                                    }
                                }
                            });
                    });
            }

            if( rlog.isTraceEnabled() ){
                rlog.trace("dump generated class");
                dump(rootClass);
            }

            if( rootConsumer!=null ){
                rootConsumer.accept(Tuple2.of(rootClass, rootMethod));
            }

            return rootClass;
        }
        private static void dump(ByteCode begin){
            if( begin==null )throw new IllegalArgumentException( "begin==null" );
            begin.walk().tree().forEach( ts -> {
                if( ts.getLevel()>0 ){
                    var pref = ts.nodes().limit(ts.getLevel()).map( b -> {
                        if( b instanceof CMethod ){
                            return CMethod.class.getSimpleName()+"#"+((CMethod) b).getName()+"()";
                        }else if( b instanceof CField ){
                            return CField.class.getSimpleName()+"#"+((CField)b).getName();
                        }
                        return b.getClass().getSimpleName();
                    }).reduce("", (a,b)->a+"/"+b);
                    rlog.trace(pref);
                }
                rlog.trace("/"+ts.getNode());
            });
        }
        //endregion
        //region classLoader : Fn1<CBegin,ClassLoader>
        public final Fn1<CBegin,ClassLoader> defaultClassLoader = cb -> new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException{
                if( name!=null && name.equals(cb.javaName().getName()) ){
                    var bytes = cb.toByteCode();
                    return defineClass(name,bytes,0,bytes.length);
                }
                return super.findClass(name);
            }
        };

        protected Fn1<CBegin,ClassLoader> classLoader = defaultClassLoader;
        public synchronized Fn1<CBegin,ClassLoader> classLoader(){
            return classLoader;
        }
        public synchronized Restore classLoader(Fn1<CBegin,ClassLoader> cl){
            if( cl==null )throw new IllegalArgumentException( "cl==null" );
            classLoader = cl;
            return this;
        }
        //endregion
        //region restoreClass()
        public synchronized Class<?> restoreClass( Consumer<CMethod> rootMethodConsumer ){
            CMethod[] rootMethArr = new CMethod[]{ null };

            var cbegin = classByteCode(r->rootMethArr[0] = r.b() );
            var rootMethod = rootMethArr[0];

            var cloader = classLoader().apply(cbegin);
            try{
                var c = Class.forName(cbegin.javaName().getName(),true,cloader);
                if( rootMethodConsumer!=null )rootMethodConsumer.accept(rootMethod);
                return c;
            } catch( ClassNotFoundException e ) {
                throw new Error(e);
            }
        }
        public synchronized Class<?> restoreClass(){
            return restoreClass(null);
        }
        //endregion
        //region restoreMethod()
        public synchronized Method method(){
            CMethod[] rootm = new CMethod[]{ null };
            Class<?> cls = restoreClass( m -> rootm[0]=m );

            CMethod rootMethod = rootm[0];

            var res = Arrays.stream(cls.getDeclaredMethods())
                .filter( m -> m.getName().equals(rootMethod.getName()))
                .findFirst();

            if( res.isEmpty() )throw new Error("method "+rootMethod.getName()+" not found in "+cls);
            return res.get();
        }
        //endregion
    }

    /**
     * Восстановление класса
     * @return восстановление
     */
    public Restore restore(){
        return new Restore(this);
    }
}
