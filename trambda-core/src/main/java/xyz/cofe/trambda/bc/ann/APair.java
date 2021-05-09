package xyz.cofe.trambda.bc.ann;

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
        throw new UnsupportedOperationException("not implemented for value="+value+" : "+value.getClass().getName());
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
