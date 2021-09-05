package xyz.cofe.trambda.tcp;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import xyz.cofe.text.Text;

/**
 * Значение заголовка TCP пакета, см {@link TcpHeader}
 * @param <T> Тип значения
 */
public interface HeaderValue<T> {
    /**
     * Имя заголовка
     * @return имя заголовка
     */
    public String getName();

    public HeaderValue<T> create(T t);
    public T getValue();

    public String asString();
    public Optional<HeaderValue<T>> parse(String string);
    public default Optional<T> parse(TcpHeader header){
        if( header==null )throw new IllegalArgumentException( "header==null" );
        var m =header.getValues();
        if( m==null )return Optional.empty();

        var str = m.get(getName());
        if( str==null )return Optional.empty();

        return parse(str).map(HeaderValue::getValue);
    }

    public static class IntValue implements HeaderValue<Integer> {
        private final String name;
        private final int value;

        public IntValue(String name,int value){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName(){
            return name;
        }

        @Override
        public HeaderValue<Integer> create(Integer value){
            if( value==null )throw new IllegalArgumentException( "value==null" );
            return new IntValue(name,value);
        }

        @Override
        public Integer getValue(){
            return value;
        }

        @Override
        public Optional<HeaderValue<Integer>> parse(String string){
            if( string==null )throw new IllegalArgumentException( "string==null" );
            try{
                int v = Integer.parseInt(string);
                return Optional.of(new IntValue(name,v));
            } catch( NumberFormatException ex ){
                return Optional.empty();
            }
        }

        @Override
        public String asString(){
            return Integer.toString(value,10);
        }

        public static IntValue create(String name, int initial){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            return new IntValue(name,initial);
        }

        public static IntValue create(String name){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            return create(name,0);
        }
    }
    public static class BoolValue implements HeaderValue<Boolean> {
        private final String name;
        private final boolean value;

        public BoolValue(String name,boolean value){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName(){
            return name;
        }

        @Override
        public BoolValue create(Boolean value){
            if( value==null )throw new IllegalArgumentException( "value==null" );
            return new BoolValue(name,value);
        }

        @Override
        public Boolean getValue(){
            return value;
        }

        @Override
        public Optional<HeaderValue<Boolean>> parse(String string){
            if( string==null )throw new IllegalArgumentException( "string==null" );
            var v = Boolean.parseBoolean(string);
            return Optional.of(new BoolValue(name,v));
        }

        @Override
        public String asString(){
            return Boolean.toString(value);
        }

        public static BoolValue create(String name, boolean initial){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            return new BoolValue(name,initial);
        }
    }
    public static class StringValue implements HeaderValue<String> {
        private final String name;
        private final String value;

        public StringValue(String name,String value){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            if( value==null )throw new IllegalArgumentException( "value==null" );
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName(){
            return name;
        }

        @Override
        public StringValue create(String value){
            if( value==null )throw new IllegalArgumentException( "value==null" );
            return new StringValue(name,value);
        }

        @Override
        public String getValue(){
            return value;
        }

        @Override
        public Optional<HeaderValue<String>> parse(String string){
            if( string==null )throw new IllegalArgumentException( "string==null" );

            string = string.trim();
            if( string.length()<1 )return Optional.of(new StringValue(name,""));

            var bytes = Text.decodeHex(string);
            if( bytes.length<1 )return Optional.of(new StringValue(name,""));

            return Optional.of(new StringValue(name,new String(bytes,StandardCharsets.UTF_8)));
        }

        @Override
        public String asString(){
            return value.length()>0 ? Text.encodeHex(value.getBytes(StandardCharsets.UTF_8)) : "";
        }

        public static StringValue create(String name, String initial){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            if( initial==null )throw new IllegalArgumentException( "initial==null" );
            return new StringValue(name,initial);
        }
    }
}
