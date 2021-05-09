package xyz.cofe.trambda.bc.cls;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.fld.FieldByteCode;

public class CField implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    public CField(){}
    public CField(int access, String name, String descriptor, String signature, Object value){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
    }
    public CField(CField sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        access = sample.getAccess();
        name = sample.getName();
        descriptor = sample.getDescriptor();
        signature = sample.getSignature();
        value = sample.getValue();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CField clone(){
        return new CField(this);
    }

    public CField configure(Consumer<CField> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region access
    protected int access;
    public int getAccess(){
        return access;
    }

    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name
    protected String name;
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor
    protected String descriptor;
    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion
    //region signature
    protected String signature;
    public String getSignature(){
        return signature;
    }

    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region value
    protected Object value;

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }
    //endregion

    @Override
    public String toString(){
        return CField.class.getSimpleName() +
            " access="+access+("#"+new AccFlags(access).flags())+
            " name=" + name +
            " descriptor=" + descriptor +
            " signature=" + signature +
            " value=" + value ;
    }

    //region fieldByteCodes : List<FieldByteCode>
    protected List<FieldByteCode> fieldByteCodes;
    public List<FieldByteCode> getFieldByteCodes(){
        if( fieldByteCodes==null )fieldByteCodes = new ArrayList<>();
        return fieldByteCodes;
    }
    public void setFieldByteCodes(List<FieldByteCode> fld){
        this.fieldByteCodes = fld;
    }
    //endregion

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var fv = v.visitField(getAccess(),getName(),getDescriptor(),getSignature(),getValue());
        var body = fieldByteCodes;
        if( body!=null ){
            for( var b : body ){
                b.write(fv);
            }
        }
    }
}
