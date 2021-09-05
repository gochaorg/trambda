package xyz.cofe.trambda.tcp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс утилита для вычисления хеша
 */
public class Hash {

    /**
     * Вычисление MD5
     * @param data данные
     * @param off смещение
     * @param size размер
     * @return результат
     */
    public static byte[] md5(byte[] data,int off,int size){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( size<0 )throw new IllegalArgumentException( "size<0" );
        if( off+size>data.length )throw new IllegalArgumentException( "off+size>data.length" );
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data,off,size);
            return md.digest();
        } catch( NoSuchAlgorithmException e ) {
            throw new UnsupportedOperationException("md5 impl not found", e);
        }
    }
}
