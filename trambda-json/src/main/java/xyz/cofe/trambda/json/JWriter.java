package xyz.cofe.trambda.json;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOError;
import java.io.IOException;

public interface JWriter {
    public JWriter value(String name, String value);
    public JWriter value(String name, int value);
    public JWriter value(String name, long value);
    public JWriter value(String name, boolean value);
    public JWriter object(String name, Object value);

    public static JWriter create(JsonGenerator gen){
        if( gen == null ) throw new IllegalArgumentException("gen==null");
        return new JWriter() {
            @Override
            public JWriter value(String name, String value){
                try{
                    if( name == null ) throw new IllegalArgumentException("name==null");
                    gen.writeStringField(name, value);
                    return this;
                } catch( IOException e ) {
                    throw new IOError(e);
                }
            }

            @Override
            public JWriter value(String name, int value){
                try{
                    if( name == null ) throw new IllegalArgumentException("name==null");
                    gen.writeNumberField(name, value);
                    return this;
                } catch( IOException e ) {
                    throw new IOError(e);
                }
            }

            @Override
            public JWriter value(String name, long value){
                try{
                    if( name == null ) throw new IllegalArgumentException("name==null");
                    gen.writeNumberField(name, value);
                    return this;
                } catch( IOException e ) {
                    throw new IOError(e);
                }
            }

            @Override
            public JWriter value(String name, boolean value){
                try{
                    if( name == null ) throw new IllegalArgumentException("name==null");
                    gen.writeBooleanField(name, value);
                    return this;
                } catch( IOException e ) {
                    throw new IOError(e);
                }
            }

            @Override
            public JWriter object(String name, Object value){
                try{
                    if( name == null ) throw new IllegalArgumentException("name==null");
                    gen.writeObjectField(name, value);
                    return this;
                } catch( IOException e ) {
                    throw new IOError(e);
                }
            }
        };
    }
}
