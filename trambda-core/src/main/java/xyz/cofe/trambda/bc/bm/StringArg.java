package xyz.cofe.trambda.bc.bm;

import java.util.List;
import java.util.stream.Collectors;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;

/**
 * Аргумент bootstrap метода
 */
public class StringArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    public StringArg(){}
    public StringArg(String v){
        value = v;
    }

    //region value : String
    private String value;
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
    //endregion

    @SuppressWarnings("ConstantConditions")
    public String toString(){
        StringBuilder sb = new StringBuilder();
        var str = value;
        if( str==null ){
            sb.append("null");
        }else{
            sb.append("\"");
            for( int i=0;i<str.length();i++ ){
                char c = str.charAt(i);
                boolean toCode = (int)c <32;
                if( toCode ){
                    sb.append("\\u");
                    String ucode = List.of(0,0,(0xff&c)>>4,(0x0f&c)).stream().map(n->Integer.toString(n,16)).collect(Collectors.joining());
                    sb.append(ucode);
                }else{
                    if( c=='\\' ){
                        sb.append("\\\\");
                    }else if( c=='\n' ){
                        sb.append("\\n");
                    }else if( c=='\r' ){
                        sb.append("\\r");
                    }else if( c=='\t' ){
                        sb.append("\\t");
                    }else {
                        sb.append(c);
                    }
                }
            }
            sb.append("\"");
        }
        return "StringArg{"+sb+"}";
    }
}
