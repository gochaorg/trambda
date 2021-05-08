package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import xyz.cofe.collection.ImTree;
import xyz.cofe.collection.Tree;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.ByteCode;

public class CBegin implements ClsByteCode, ImTree<ByteCode> {
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
        interfaces = sample.getInterfaces();
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
            ", access=" + access +
            ", name='" + name + '\'' +
            ", signature='" + signature + '\'' +
            ", superName='" + superName + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
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
}
