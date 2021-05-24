package xyz.cofe.trambda.bc.ann;

import java.io.Serializable;
import org.objectweb.asm.AnnotationVisitor;

public abstract class APair<V> extends AAbstractBC implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    protected APair(String name, V value){
        this.name = name;
        this.value = value;
    }

    //region name : String
    protected String name;
    public String getName(){ return name; }
    protected void setName(String s){ this.name = s; }
    //endregion
    //region value : V
    protected V value;
    public V getValue(){ return value; }
    public void setValue(V v){ value=v; }
    //endregion

    public abstract APair<V> clone();

    @Override
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visit(getName(), getValue());
    }

    public String toString(){
        return
            this.getClass().getSimpleName()+
                " name="+name+
                " value="+(value instanceof String ? "\""+value+"\"" : value)+
                "";
    }

    public static APair<?> create(String name, Object value){
        if( value==null )throw new IllegalArgumentException( "value==null" );
        if( value instanceof String ){
            return new APairString(name, (String)value);
        }else if( value instanceof Boolean ){
            return new APairBoolean(name, (Boolean) value);
        }else if( value instanceof Byte ){
            return new APairByte(name, (Byte) value);
        }else if( value instanceof Character ){
            return new APairCharacter(name, (Character) value);
        }else if( value instanceof Short ){
            return new APairShort(name, (Short) value);
        }else if( value instanceof Integer ){
            return new APairInteger(name, (Integer) value);
        }else if( value instanceof Long ){
            return new APairLong(name, (Long) value);
        }else if( value instanceof Float ){
            return new APairFloat(name, (Float) value);
        }else if( value instanceof Double ){
            return new APairDouble(name, (Double) value);
        }

        var cls = value.getClass();
        var clsName = cls.getName();
        if( cls.isArray() ){
            if( clsName.equals("[Z") )return new APairBooleanArr1D( name, (boolean[])value );
            if( clsName.equals("[C") )return new APairCharArr1D( name, (char[])value );
            if( clsName.equals("[B") )return new APairByteArr1D( name, (byte[])value );
            if( clsName.equals("[S") )return new APairShortArr1D( name, (short[])value );
            if( clsName.equals("[I") )return new APairIntArr1D( name, (int[])value );
            if( clsName.equals("[F") )return new APairFloatArr1D( name, (float[])value );
            if( clsName.equals("[J") )return new APairLongArr1D( name, (long[])value );
            if( clsName.equals("[D") )return new APairDoubleArr1D( name, (double[])value );
        }

        if( value instanceof Serializable ){
            return new APairSerializable(name, (Serializable) value);
        }

        throw new UnsupportedOperationException("not implemented for value="+value+" : "+value.getClass().getName());
    }

    // Z - boolean
    // C - char
    // B - byte
    // S - short
    // I - int
    // F - float
    // J - long
    // D - double
    // V - void
    // L - object

    public static class APairBooleanArr1D extends APair<boolean[]> {
        public APairBooleanArr1D(String name, boolean[] value){
            super(name, value);
        }

        @Override
        public APairBooleanArr1D clone(){
            return new APairBooleanArr1D(getName(), getValue());
        }
    }
    public static class APairCharArr1D extends APair<char[]> {
        public APairCharArr1D(String name, char[] value){
            super(name, value);
        }

        @Override
        public APairCharArr1D clone(){
            return new APairCharArr1D(getName(), getValue());
        }
    }
    public static class APairByteArr1D extends APair<byte[]> {
        public APairByteArr1D(String name, byte[] value){
            super(name, value);
        }

        @Override
        public APairByteArr1D clone(){
            return new APairByteArr1D(getName(), getValue());
        }
    }
    public static class APairShortArr1D extends APair<short[]> {
        public APairShortArr1D(String name, short[] value){
            super(name, value);
        }

        @Override
        public APairShortArr1D clone(){
            return new APairShortArr1D(getName(), getValue());
        }
    }
    public static class APairIntArr1D extends APair<int[]> {
        public APairIntArr1D(String name, int[] value){
            super(name, value);
        }

        @Override
        public APair<int[]> clone(){
            return new APairIntArr1D(getName(), getValue());
        }
    }
    public static class APairFloatArr1D extends APair<float[]> {
        public APairFloatArr1D(String name, float[] value){
            super(name, value);
        }

        @Override
        public APairFloatArr1D clone(){
            return new APairFloatArr1D(getName(), getValue());
        }
    }
    public static class APairLongArr1D extends APair<long[]> {
        public APairLongArr1D(String name, long[] value){
            super(name, value);
        }

        @Override
        public APairLongArr1D clone(){
            return new APairLongArr1D(getName(), getValue());
        }
    }
    public static class APairDoubleArr1D extends APair<double[]> {
        public APairDoubleArr1D(String name, double[] value){
            super(name, value);
        }

        @Override
        public APairDoubleArr1D clone(){
            return new APairDoubleArr1D(getName(), getValue());
        }
    }

    public static class APairSerializable extends APair<Serializable> {
        public APairSerializable(String name, Serializable value){
            super(name, value);
        }

        @Override
        public APairSerializable clone(){
            return new APairSerializable(getName(), getValue());
        }
    }

    public static class APairString extends APair<String> {
        public APairString(String name, String value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairString clone(){
            return new APairString(getName(),getValue());
        }
    }
    public static class APairByte extends APair<Byte> {
        public APairByte(String name, Byte value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairByte clone(){
            return new APairByte(getName(),getValue());
        }
    }
    public static class APairBoolean extends APair<Boolean> {
        public APairBoolean(String name, Boolean value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairBoolean clone(){
            return new APairBoolean(getName(),getValue());
        }
    }
    public static class APairCharacter extends APair<Character> {
        public APairCharacter(String name, Character value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairCharacter clone(){
            return new APairCharacter(getName(),getValue());
        }
    }
    public static class APairShort extends APair<Short> {
        public APairShort(String name, Short value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairShort clone(){
            return new APairShort(getName(),getValue());
        }
    }
    public static class APairInteger extends APair<Integer> {
        public APairInteger(String name, Integer value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairInteger clone(){
            return new APairInteger(getName(),getValue());
        }
    }
    public static class APairLong extends APair<Long> {
        public APairLong(String name, Long value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairLong clone(){
            return new APairLong(getName(),getValue());
        }
    }
    public static class APairFloat extends APair<Float> {
        public APairFloat(String name, Float value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairFloat clone(){
            return new APairFloat(getName(),getValue());
        }
    }
    public static class APairDouble extends APair<Double> {
        public APairDouble(String name, Double value){
            super(name, value);
        }

        @Override
        public void write(AnnotationVisitor v){
            if( v==null )throw new IllegalArgumentException( "v==null" );
            v.visit(getName(), getValue());
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public APairDouble clone(){
            return new APairDouble(getName(),getValue());
        }
    }
}
