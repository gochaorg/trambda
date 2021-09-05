package xyz.cofe.trambda.tcp.ssl;

public class Bytes {
    public static byte[] join( byte[] a, int aOffset, int aLen, byte[] b, int bOffset, int bLen){
        byte[] buff = new byte[aLen+bLen];
        System.arraycopy(a,aOffset,buff,0,aLen);
        System.arraycopy(b,bOffset,buff,aLen,bLen);
        return buff;
    }
    public static byte[] join( byte[] a, byte[] b ){
        return join(a,0,a.length, b,0,b.length);
    }
}
