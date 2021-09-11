package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.LdcType;
import xyz.cofe.trambda.bc.bm.MHandle;

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.ldc">Push item from run-time constant pool</a>
 * <p> Описание
 * <p> The index is an unsigned byte that must be a valid index into the run-time constant pool of the current class (§2.6). The run-time constant pool entry at index either must be a run-time constant of type int or float, or a reference to a string literal, or a symbolic reference to a class, method type, or method handle (§5.1).
 * <p> If the run-time constant pool entry is a run-time constant of type int or float, the numeric value of that run-time constant is pushed onto the operand stack as an int or float, respectively.
 * <p> Otherwise, if the run-time constant pool entry is a reference to an instance of class String representing a string literal (§5.1), then a reference to that instance, value, is pushed onto the operand stack.
 * <p> Otherwise, if the run-time constant pool entry is a symbolic reference to a class (§5.1), then the named class is resolved (§5.4.3.1) and a reference to the Class object representing that class, value, is pushed onto the operand stack.
 * <p> Otherwise, the run-time constant pool entry must be a symbolic reference to a method type or a method handle (§5.1). The method type or method handle is resolved (§5.4.3.5) and a reference to the resulting instance of java.lang.invoke.MethodType or java.lang.invoke.MethodHandle, value, is pushed onto the operand stack.
 * <hr>
 * Индекс - это беззнаковый байт(или слово), который должен быть допустимым индексом в пуле констант времени выполнения текущего класса (§2.6). Запись пула констант времени выполнения по индексу должна быть либо константой времени выполнения типа int или float, либо ссылкой на строковый литерал, либо символьной ссылкой на класс, тип метода или дескриптор метода (§5.1).
 * <p> Если запись пула констант времени выполнения является константой времени выполнения типа int или float, числовое значение этой константы времени выполнения помещается в стек операндов как int или float соответственно.
 * <p> В противном случае, если запись пула констант времени выполнения является ссылкой на экземпляр класса String, представляющий строковый литерал (§5.1), то ссылка на этот экземпляр value помещается в стек операндов.
 * <p> В противном случае, если запись пула констант времени выполнения является символической ссылкой на класс (§5.1), то именованный класс разрешается (§5.4.3.1) и ссылка на объект Class, представляющий этот класс, значение, помещается в стек операндов.
 * <p> В противном случае запись пула констант времени выполнения должна быть символьной ссылкой на тип метода или дескриптор метода (§5.1). Тип метода или дескриптор метода разрешается (§5.4.3.5), и ссылка на результирующий экземпляр java.lang.invoke.MethodType или java.lang.invoke.MethodHandle, value, помещается в стек операндов.
 * <hr>
 * 
 * LDC instruction. Note that new constant types may be added in future versions of the
 * Java Virtual Machine. To easily detect new constant types, implementations of this method
 * should check for unexpected constant types, like this:
 * 
* <pre>
* if (cst instanceof Integer) {
*     // ... {@link LdcType#Integer}
* } else if (cst instanceof Float) {
*     // ... {@link LdcType#Float}
* } else if (cst instanceof Long) {
*     // ... {@link LdcType#Long}
* } else if (cst instanceof Double) {
*     // ... {@link LdcType#Double}
* } else if (cst instanceof String) {
*     // ... {@link LdcType#String}
* } else if (cst instanceof org.objectweb.asm.Type) {
*     int sort = ((org.objectweb.asm.Type) cst).getSort();
*     if (sort == org.objectweb.asm.Type.OBJECT) {
*         // ... {@link LdcType#Object}
*     } else if (sort == org.objectweb.asm.Type.ARRAY) {
*         // ... {@link LdcType#Array}
*     } else if (sort == org.objectweb.asm.Type.METHOD) {
*         // ... {@link LdcType#Method}
*     } else {
*         // throw an exception 
*     }
* } else if (cst instanceof Handle) {
*     // ... {@link LdcType#Handle}
* } else if (cst instanceof ConstantDynamic) {
*     // ... {@link LdcType#ConstantDynamic}
* } else {
*     // throw an exception
* }
* </pre>
 */
public class MLdcInsn extends MAbstractBC implements MethodWriter {
    /**
     * Конструктор по умолчанию
     */
    public MLdcInsn(){
    }
    public MLdcInsn(Object value, LdcType ldcType){
        this.value = value;
        this.ldcType = ldcType;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
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
