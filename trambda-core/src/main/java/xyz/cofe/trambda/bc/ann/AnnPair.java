package xyz.cofe.trambda.bc.ann;

import xyz.cofe.trambda.bc.ByteCode;

public abstract class AnnPair<V> extends AnnAbstractBC {
    private static final long serialVersionUID = 1;

    protected AnnPair(String name,V value){
        this.name = name;
        this.value = value;
    }

    protected String name;
    public String getName(){ return name; }
    protected void setName(String s){ this.name = s; }

    protected V value;
    public V getValue(){ return value; }
    public void setValue(V v){ value=v; }

    public String toString(){
        return this.getClass().getSimpleName()+" name="+name+" value="+value;
    }

    public static AnnPair<?> create(String name, Object value){
        if( value==null )throw new IllegalArgumentException( "value==null" );
        if( value instanceof String ){
            return new AnnPairString(name, (String)value);
        }else if( value instanceof Boolean ){
            return new AnnPairBoolean(name, (Boolean) value);
        }else if( value instanceof Byte ){
            return new AnnPairByte(name, (Byte) value);
        }else if( value instanceof Character ){
            return new AnnPairCharacter(name, (Character) value);
        }else if( value instanceof Short ){
            return new AnnPairShort(name, (Short) value);
        }else if( value instanceof Integer ){
            return new AnnPairInteger(name, (Integer) value);
        }else if( value instanceof Long ){
            return new AnnPairLong(name, (Long) value);
        }else if( value instanceof Float ){
            return new AnnPairFloat(name, (Float) value);
        }else if( value instanceof Double ){
            return new AnnPairDouble(name, (Double) value);
        }
        throw new UnsupportedOperationException("not implemented for value="+value+" : "+value.getClass().getName());
    }

    public static class AnnPairString extends AnnPair<String> {
        public AnnPairString(String name, String value){
            super(name, value);
        }
    }
    public static class AnnPairByte extends AnnPair<Byte> {
        public AnnPairByte(String name, Byte value){
            super(name, value);
        }
    }
    public static class AnnPairBoolean extends AnnPair<Boolean> {
        public AnnPairBoolean(String name, Boolean value){
            super(name, value);
        }
    }
    public static class AnnPairCharacter extends AnnPair<Character> {
        public AnnPairCharacter(String name, Character value){
            super(name, value);
        }
    }
    public static class AnnPairShort extends AnnPair<Short> {
        public AnnPairShort(String name, Short value){
            super(name, value);
        }
    }
    public static class AnnPairInteger extends AnnPair<Integer> {
        public AnnPairInteger(String name, Integer value){
            super(name, value);
        }
    }
    public static class AnnPairLong extends AnnPair<Long> {
        public AnnPairLong(String name, Long value){
            super(name, value);
        }
    }
    public static class AnnPairFloat extends AnnPair<Float> {
        public AnnPairFloat(String name, Float value){
            super(name, value);
        }
    }
    public static class AnnPairDouble extends AnnPair<Double> {
        public AnnPairDouble(String name, Double value){
            super(name, value);
        }
    }
}
