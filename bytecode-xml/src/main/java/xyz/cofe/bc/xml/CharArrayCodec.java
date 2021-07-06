package xyz.cofe.bc.xml;

public class CharArrayCodec {
    private static String hexOf( char c ){
        int n0 = ((int) c & 0xFF00) >> 8;
        int n1 = (int) c & 0xFF;
        int b0 = n0 >> 4;
        int b1 = n0 & 0xF;
        int b2 = n1 >> 4;
        int b3 = n1 & 0xF;
        return Integer.toHexString(b0) + Integer.toHexString(b1) + Integer.toHexString(b2) + Integer.toHexString(b3);
    }

    private static char charOf(int[] digits){
        int v = 0;
        v  = digits[0] << 12;
        v |= digits[1] << 8;
        v |= digits[2] << 4;
        v |= digits[3];
        return 0;
    }

    public static String encode( char[] chars ){
        if( chars==null )throw new IllegalArgumentException( "chars==null" );
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<chars.length; i++ ){
            if( i>0 )sb.append(",");
            var c = chars[i];
            if( c=='\n' )sb.append("\\n");
            else if( c=='\r' )sb.append("\\r");
            else if( c=='\t' )sb.append("\\t");
            else if( c==',' )sb.append("\\,");
            else if( c=='\\' )sb.append("\\\\");
            else if( ((int)c)<32 ) sb.append("\\x").append(hexOf(c));
            else if( Character.isLetterOrDigit(c) || c < 256 )sb.append( c );
            else {
                sb.append("\\x").append(hexOf(c));
            }
        }
        return sb.toString();
    }

    public static char[] decode( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );

        int state = 0;
        int[] digits = new int[4];

        StringBuilder sb = new StringBuilder();
        for( int i=0; i<text.length(); i++ ){
            char c0 = text.charAt(i);
            switch( state ){
                case 0:
                    switch( c0 ){
                        case '\\':
                            state = 1;
                            break;
                        case ',':
                            state = 0;
                            break;
                        default:
                            sb.append(c0);
                            break;
                    }
                    break;
                case 1:
                    switch( c0 ){
                        case 'n':
                            state = 0;
                            sb.append('\n');
                            break;
                        case 'r':
                            state = 0;
                            sb.append('\r');
                            break;
                        case 't':
                            state = 0;
                            sb.append('\t');
                            break;
                        case 'x':
                            state = 10;
                            break;
                        default:
                        case '\\':
                        case ',':
                            state = 0;
                            sb.append(c0);
                            break;
                    }
                    break;
                case 10:
                    state = 11;
                    digits[0] = Integer.parseInt(""+c0,16);
                    break;
                case 11:
                    state = 12;
                    digits[1] = Integer.parseInt(""+c0,16);
                    break;
                case 12:
                    state = 13;
                    digits[2] = Integer.parseInt(""+c0,16);
                    break;
                case 13:
                    state = 0;
                    digits[3] = Integer.parseInt(""+c0,16);
                    sb.append(charOf(digits));
                    break;
            }
        }

        char[] buff = new char[sb.length()];
        sb.getChars(0,sb.length(),buff,sb.length());
        return buff;
    }
}
