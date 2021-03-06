package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.LdcType;
import xyz.cofe.trambda.bc.bm.MHandle;

public class MLdcInsn extends MAbstractBC implements MethodWriter {
    public MLdcInsn(){
    }
    public MLdcInsn(Object value, LdcType ldcType){
        this.value = value;
        this.ldcType = ldcType;
    }
    public MLdcInsn(MLdcInsn sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        ldcType = sample.getLdcType();
        if( sample.value instanceof BootstrapMethArg ){
            value = ((BootstrapMethArg)sample.value).clone();
        }else{
            value = sample.value;
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLdcInsn clone(){ return new MLdcInsn(this); }

    //region ldcType : LdcType
    private LdcType ldcType;
    public LdcType getLdcType(){
        return ldcType;
    }
    public void setLdcType(LdcType ldcType){
        this.ldcType = ldcType;
    }
    //endregion
    //region value
    private Object value;
    public Object getValue(){
        return value;
    }
    public void setValue(Object value){
        this.value = value;
    }
    //endregion
    public String toString(){
        return MLdcInsn.class.getSimpleName()+
            " ldcType="+ldcType+
            " value="+value;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        switch( getLdcType() ){
            case Long: v.visitLdcInsn((Long)getValue()); break;
            case Integer: v.visitLdcInsn((Integer)getValue()); break;
            case Double: v.visitLdcInsn((Double)getValue()); break;
            case String: v.visitLdcInsn((String)getValue()); break;
            case Float: v.visitLdcInsn((Float)getValue()); break;
            case Handle:
                var hdl1 = (MHandle)getValue();
                var hdl0 = new org.objectweb.asm.Handle(
                    hdl1.getTag(), hdl1.getOwner(), hdl1.getName(), hdl1.getDesc(), hdl1.isIface()
                );
                v.visitLdcInsn(hdl0);
                break;
            case Array:
            case Method:
            case Object:
                if( value==null )throw new IllegalStateException("value is null");
                if( value instanceof Type ){
                    v.visitLdcInsn(value);
                }else{
                    v.visitLdcInsn(Type.getType(value.toString()));
                }
                break;
            default:
                throw new UnsupportedOperationException("not impl for ldc type = "+getLdcType());
        }
    }
}
