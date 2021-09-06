package xyz.cofe.trambda.bc;

import java.util.Optional;
import xyz.cofe.fn.Tuple2;

/**
 * Кодирование - декодирование строчных литералов
 */
public class StringLiteral {
    /**
     * Кодирование строки
     * @param str строка, возможно null
     * @return литерал
     */
    public static String toStringLiteral( String str ){
        if( str==null )return "null";

        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for( int i=0; i<str.length(); i++ ){
            char c = str.charAt(i);
            if( c=='\\' ){
                sb.append("\\\\");
            }else if( c=='\'' ){
                sb.append("\\'");
            }else if( c=='"' ){
                sb.append("\\\"");
            }else if( c=='\n' ){
                sb.append("\\n");
            }else if( c=='\r' ){
                sb.append("\\r");
            }else if( c=='\t' ){
                sb.append("\\t");
            }else if( ((int)c)<32 ){
                sb.append("\\x");
                int b = c & 0xff;
                int lo = b & 0xf;
                int hi = (b & 0xf0) >> 4;
                sb.append("00").append(Integer.toHexString(hi)).append(Integer.toHexString(lo));
            }else if( Character.isLetterOrDigit(c) ){
                sb.append(c);
            }else if( ((int)c)<256 ){
                sb.append(c);
            }else {
                int n = (int)c;

                int b0 = (n & 0xFF00) >> 8;
                int b1 = n & 0x00FF;

                int n0 = (b0 & 0xF0) >> 4;
                int n1 = (b0 & 0xF);

                int n2 = (b1 & 0xF0) >> 4;
                int n3 = (b1 & 0xF);

                sb.append("\\x");
                sb.append(Integer.toHexString(n0));
                sb.append(Integer.toHexString(n1));
                sb.append(Integer.toHexString(n2));
                sb.append(Integer.toHexString(n3));
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    /**
     * Декодирование литерала
     * @param text текст
     * @param offset смещение в тексте
     * @return литерал и смещение - конец литерала
     */
    public static Optional<Tuple2<String,Integer>> parse( String text, int offset ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
        if( offset>=text.length() ){
            return Optional.empty();
        }

        String str = text.substring(offset);
        if( str.startsWith("null") )return Optional.of(Tuple2.of(null,offset+4));
        if( !str.startsWith("\"") )return Optional.empty();

        StringBuilder sb = new StringBuilder();
        int ptr = 1;
        var state = "init";

        StringBuilder hex = new StringBuilder();

        while( true ){
            if( ptr>=str.length() )break;

            char c0 = str.charAt(ptr);
            ptr++;

            boolean stop = false;

            switch( state ){
                case "init":
                    switch( c0 ){
                        case '"':
                            stop = true;
                            break;
                        case '\\':
                            state = "escape";
                            break;
                        default:
                            sb.append(c0);
                            break;
                    }
                    break;
                case "escape":
                    switch( c0 ){
                        case 'n':
                            state = "init";
                            sb.append("\n");
                            break;
                        case 'r':
                            state = "init";
                            sb.append("\r");
                            break;
                        case 't':
                            state = "init";
                            sb.append("\t");
                            break;
                        case 'x':
                            state = "hex0";
                            hex.setLength(0);
                            break;
                        default:
                            sb.append(c0);
                            state = "init";
                            break;
                    }
                    break;
                case "hex0": hex.append(c0); state="hex1"; break;
                case "hex1": hex.append(c0); state="hex2"; break;
                case "hex2": hex.append(c0); state="hex3"; break;
                case "hex3":
                    hex.append(c0);
                    state="init";
                    int n = Integer.parseInt(hex.toString());
                    sb.append((char)n);
                    break;
            }

            if( stop )break;
        }

        int next = ptr+offset;
        return Optional.of(Tuple2.of(sb.toString(),next));
    }
}
