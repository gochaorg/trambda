package xyz.cofe.trambda.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import xyz.cofe.text.Text;

public interface Message extends Serializable {
    public static byte[] serialize(Message message){
        if( message==null )throw new IllegalArgumentException( "message==null" );
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try{
            ObjectOutputStream out = new ObjectOutputStream(ba);
            out.writeObject(message);
            out.flush();
            out.close();
        } catch( IOException e ) {
            throw new IOError(e);
        }
        byte[] data = ba.toByteArray();
        return data;
    }
    public static Message deserialize(byte[] bytes){
        if( bytes==null )throw new IllegalArgumentException( "bytes==null" );
        ByteArrayInputStream ba = new ByteArrayInputStream(bytes);
        try{
            ObjectInputStream input = new ObjectInputStream(ba);
            Object obj = input.readObject();
            return (Message)obj;
        } catch( IOException | ClassNotFoundException e ) {
            throw new IOError(e);
        }
    }
}
