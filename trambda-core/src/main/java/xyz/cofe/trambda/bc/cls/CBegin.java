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
    private static final long serialVersionUID = 1;

    public CBegin(){}
    public CBegin(int version, int access, String name, String signature, String superName, String[] interfaces){
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }
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

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CBegin clone(){
        return new CBegin(this);
    }

    public CBegin configure(Consumer<CBegin> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region version : int
    protected int version;
    public int getVersion(){
        return version;
    }
    public void setVersion(int version){
        this.version = version;
    }
    //endregion
    //region access : int
    protected int access;
    public int getAccess(){
        return access;
    }
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name : String
    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public static class JavaNamed {
        public final CBegin cBegin;
        public JavaNamed(CBegin cBegin){
            if( cBegin==null )throw new IllegalArgumentException( "cBegin==null" );
            this.cBegin = cBegin;
        }

        //region name : String
        public String getName(){
            var n = cBegin.getName();
            if( n==null )return null;
            return new JavaClassName(n).name;
        }

        public void setName(String name){
            if( name==null ){
                cBegin.setName( null );
            }else {
                cBegin.setName( new JavaClassName(name).rawName() );
            }
        }
        //endregion
        //region simpleName : String
        public String getSimpleName(){
            var n = cBegin.getName();
            if( n==null )return null;

            return new JavaClassName(n).simpleName;
        }

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
        //region package : String
        public String getPackage(){
            var n = cBegin.getName();
            if( n==null )return "";

            return new JavaClassName(n).packageName;
        }

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
    public JavaNamed javaName(){
        return new JavaNamed(this);
    }
    //endregion
    //region signature : String
    protected String signature;
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region superName : String
    protected String superName;
    public String getSuperName(){
        return superName;
    }
    public void setSuperName(String superName){
        this.superName = superName;
    }
    //endregion
    //region interfaces : String[]
    protected String[] interfaces;
    public String[] getInterfaces(){
        return interfaces;
    }
    public void setInterfaces(String[] interfaces){
        this.interfaces = interfaces;
    }
    //endregion

    //region source : CSource
    protected CSource source;
    public CSource getSource(){ return source; }
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

    //region annotations : List<CAnnotation>
    protected List<CAnnotation> annotations;
    public List<CAnnotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }
    public void setAnnotations(List<CAnnotation> ls){
        annotations = ls;
    }
    //endregion
    //region typeAnnotations : List<CTypeAnnotation>
    protected List<CTypeAnnotation> typeAnnotations;
    public List<CTypeAnnotation> getTypeAnnotations(){
        if( typeAnnotations==null )typeAnnotations = new ArrayList<>();
        return typeAnnotations;
    }
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

    //region fields : List<CField>
    protected List<CField> fields;
    public List<CField> getFields(){
        if( fields==null )fields = new ArrayList<>();
        return fields;
    }
    public void setFields(List<CField> fields){
        this.fields = fields;
    }
    //endregion
    //region methods : List<CMethod>
    protected List<CMethod> methods;
    public List<CMethod> getMethods(){
        if( methods==null )methods = new ArrayList<>();
        return methods;
    }
    public void setMethods(List<CMethod> methods){
        this.methods = methods;
    }
    //endregion
    //region order : Map<ClsByteCode,Integer>
    protected Map<ClsByteCode,Integer> order;
    public Map<ClsByteCode,Integer> getOrder(){
        if( order==null )order = new LinkedHashMap<>();
        return order;
    }
    public void setOrder(Map<ClsByteCode,Integer> order){
        this.order = order;
    }
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
    @Override
    public void write(ClassWriter v){
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

    public byte[] toByteCode(){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        write(cw);
        return cw.toByteArray();
    }

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

    public static CBegin parseByteCode(Class<?> clazz){
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );

        var resName = "/"+clazz.getName().replace(".","/")+".class";
        var classUrl = clazz.getResource(resName);

        if( classUrl==null )throw new IOError(
            new IOException("resource "+resName+" not found")
        );

        return parseByteCode(classUrl);
    }

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
