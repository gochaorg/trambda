package xyz.cofe.bc.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import xyz.cofe.text.Text;

public class HexCodec {
    public static String serialize(Serializable serializable){
        if( serializable==null )throw new IllegalArgumentException( "serializable==null" );
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oout = new ObjectOutputStream(ba);
            oout.writeObject(serializable);
            oout.flush();
        } catch( IOException e ) {
            throw new IOError(e);
        }
        return Text.encodeHex(ba.toByteArray());
    }
    public static <A> A deserialize(String text){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        if( text.length()<1 )return null;

        var bytes = Text.decodeHex(text);
        ByteArrayInputStream ba = new ByteArrayInputStream(bytes);

        try{
            ObjectInputStream oinput = new ObjectInputStream(ba);
            var obj = oinput.readObject();
            //noinspection unchecked
            return (A)obj;
        } catch( IOException | ClassNotFoundException e ) {
            throw new IOError(e);
        }
    }

    public static <A> A deserialize(Class<A> cls,String text){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        return deserialize(text);
    }
}
