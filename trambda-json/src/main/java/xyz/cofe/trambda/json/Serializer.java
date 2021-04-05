package xyz.cofe.trambda.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.function.BiConsumer;

public class Serializer<T> {
    public static final String TYPE="@type";
    public final Class<T> clazz;

    public Serializer(Class<T> c){
        this.clazz = c;
    }

    private BiConsumer<JWriter, T> writer;

    public Serializer<T> write(BiConsumer<JWriter, T> writer){
        this.writer = writer;
        return this;
    }

    public StdSerializer<T> build(){
        return new StdSerializer<T>(clazz) {
            @Override
            public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException{
                gen.writeStartObject();
                gen.writeStringField(TYPE, clazz.getSimpleName());
                if( writer != null ){
                    writer.accept(JWriter.create(gen), value);
                }
                gen.writeEndObject();
            }
        };
    }

    public void registry(SimpleModule mod){
        if( mod == null ) throw new IllegalArgumentException("mod==null");
        mod.addSerializer(clazz, build());
    }
}
