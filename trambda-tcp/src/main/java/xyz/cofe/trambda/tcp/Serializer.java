package xyz.cofe.trambda.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Простая сериализация объекта в байты и обратно.
 * Используется стандартные средства сериализации {@link ObjectInputStream}
 */
public class Serializer {
    /**
     * Сериализация объекта в массив байтов
     * @param obj объект
     * @return массив байтов
     */
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

    /**
     * Десериализация из массива байтов в объект
     * @param bytes массив байтов
     * @param <T> тип объекта
     * @return объект
     */
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
