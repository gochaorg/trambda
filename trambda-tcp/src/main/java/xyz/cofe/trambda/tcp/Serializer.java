package xyz.cofe.trambda.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
    public static byte[] toBytes(Object obj){
        if( obj==null )throw new IllegalArgumentException( "obj==null" );
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try{
            ObjectOutputStream out = new ObjectOutputStream(ba);
            out.writeObject(obj);
            out.flush();
            return ba.toByteArray();
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    public static <T> T fromBytes(byte[] bytes){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        ByteArrayInputStream ba = new ByteArrayInputStream(bytes);
        try{
            ObjectInputStream input = new ObjectInputStream(ba);
            return (T)input.readObject();
        } catch( IOException e ) {
            throw new IOError(e);
        } catch( ClassNotFoundException e ) {
            throw new RuntimeException(e);
        }
    }
}
