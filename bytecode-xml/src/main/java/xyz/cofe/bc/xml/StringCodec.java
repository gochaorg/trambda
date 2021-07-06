package xyz.cofe.bc.xml;

public class StringCodec {
    public static String hexOf( char c ){
        int n0 = ((int) c & 0xFF00) >> 8;
        int n1 = (int) c & 0xFF;
        int b0 = n0 >> 4;
        int b1 = n0 & 0xF;
        int b2 = n1 >> 4;
        int b3 = n1 & 0xF;
        return Integer.toHexString(b0) + Integer.toHexString(b1) + Integer.toHexString(b2) + Integer.toHexString(b3);
    }

    public static String htmlEncode1(String text){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<text.length(); i++ ){
            char c0 = text.charAt(i);
            if( c0<32 ){
                sb.append("&#x").append(hexOf(c0)).append(";");
            }else if( Character.isLetterOrDigit(c0) || c0<256 ){
                sb.append(c0);
            }else {
                sb.append("&#x").append(hexOf(c0)).append(";");
            }
        }
        return sb.toString();
    }
}
