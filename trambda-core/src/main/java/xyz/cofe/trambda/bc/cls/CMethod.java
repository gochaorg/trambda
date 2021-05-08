package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import xyz.cofe.iter.Eterable;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.mth.MethodByteCode;
import xyz.cofe.trambda.bc.mth.MthVisIdProperty;

public class CMethod implements ClsByteCode, MthVisIdProperty {
    private static final long serialVersionUID = 1;

    public CMethod(){}

    public CMethod(int access, String name, String descriptor, String signature, String[] exceptions){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    public CMethod(CMethod sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        access = sample.getAccess();
        name = sample.getName();
        descriptor = sample.getDescriptor();
        signature = sample.getSignature();
        exceptions = sample.getExceptions();
        methodVisitorId = sample.methodVisitorId;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CMethod clone(){
        return new CMethod(this);
    }

    public CMethod configure(Consumer<CMethod> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

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
    //region descriptor : String
    protected String descriptor;
    public String getDescriptor(){
        return descriptor;
    }
    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
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
    //region exceptions : String[]
    protected String[] exceptions;
    public String[] getExceptions(){
        return exceptions;
    }
    public void setExceptions(String[] exceptions){
        this.exceptions = exceptions;
    }
    //endregion
    //region methodVisitorId : int = MthVisIdProperty.DEF_METHOD_VISITOR_ID
    protected int methodVisitorId = MthVisIdProperty.DEF_METHOD_VISITOR_ID;

    @Override
    public int getMethodVisitorId(){
        return methodVisitorId;
    }

    @Override
    public void setMethodVisitorId(int id){
        methodVisitorId = id;
    }
    //endregion
    //region methodByteCodes : List<MethodByteCode>
    protected List<MethodByteCode> methodByteCodes;
    public List<MethodByteCode> getMethodByteCodes(){
        if( methodByteCodes==null )methodByteCodes = new ArrayList<>();
        return methodByteCodes;
    }
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
}
