package xyz.cofe.lang.basic;

import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import xyz.cofe.lang.basic.BasicParser.*;

/**
 * Простой интерпретатор
 */
public class Interpetator {
    public Object eval(RuleNode node){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( node instanceof LiteralContext ) return eval((LiteralContext) node);
        if( node instanceof LiteralValueContext ) return eval( ((LiteralValueContext)node).literal() );
        if( node instanceof ParenthesesContext ) return eval( ((ParenthesesContext)node).expr() );
        if( node instanceof UnaryOpContext ) return eval( (UnaryOpContext)node );
        if( node instanceof BinOpContext ) return eval( (BinOpContext)node );
        if( node instanceof RContext ) return eval( ((RContext)node).expr() );
        return null;
    }

    public Object eval( LiteralContext lit ){
        if( lit==null )throw new IllegalArgumentException("lit == null");

        var v_num = lit.NUMBER();
        if( v_num!=null )return evalNumber(v_num);

        var v_str = lit.STRING();
        if( v_str!=null )return evalString(v_str);

        return null;
    }
    public Number evalNumber(TerminalNode node){
        if( node==null )throw new IllegalArgumentException( "node==null" );
        return Double.valueOf(node.getText());
    }
    public String evalString(TerminalNode node){
        if( node==null )throw new IllegalArgumentException( "node==null" );
        String raw = node.getText();

        StringBuilder sb = new StringBuilder();
        var state = "def";
        int hex0 = 0;
        int hex1 = 0;
        int hex2 = 0;
        int hex3 = 0;

        for( var i=1; i<raw.length()-1; i++ ){
            var c = raw.charAt(i);
            switch (state){
                case "def":
                    if (c == '\\') {
                        state = "escape";
                    } else {
                        sb.append(c);
                    }
                    break;
                case "escape":
                    switch (c){
                        case 'n':
                            state = "def";
                            sb.append("\n");
                            break;
                        case 'r':
                            state = "def";
                            sb.append("\r");
                            break;
                        case 't':
                            state = "def";
                            sb.append("\t");
                            break;
                        case 'x':
                            state = "hex0";
                            sb.append("\t");
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    break;
                case "hex0":
                    hex0 = Integer.parseInt(""+c,16);
                    state = "hex1";
                    break;
                case "hex1":
                    hex1 = Integer.parseInt(""+c,16);
                    state = "hex2";
                    break;
                case "hex2":
                    hex2 = Integer.parseInt(""+c,16);
                    state = "hex3";
                    break;
                case "hex3":
                    hex3 = Integer.parseInt(""+c,16);
                    state = "def";
                    int char_code = (hex0 << 4*3) | (hex1 << 4*2) | (hex2 << 4) | hex3;
                    sb.append((char)char_code);
                    break;
            }
        }
        return sb.toString();
    }

    public Object eval( UnaryOpContext node ){
        if( node==null )throw new IllegalArgumentException( "node==null" );
        var op = node.op!=null ? node.op.getText() : "";
        var value = eval(node.expr());
        switch (op){
            case "!":
                if( value instanceof Boolean ) return negative((Boolean) value);
                return null;
            case "-":
                if( value instanceof Double ) return negative((Double) value);
                return null;
            case "+":
                return value;
        }

        return null;
    }

    public Boolean negative(Boolean value){ return value!=null ? !value : null; }
    public Double negative(Double value){ return value!=null ? -value : null; }

    public Object eval( BinOpContext node ){
        if( node==null )throw new IllegalArgumentException( "node==null" );
        var op = node.op!=null ? node.op.getText() : "";
        var left = eval(node.left);
        var right = eval(node.right);
        switch (op){
            case "+": return add(left,right);
            case "-": return sub(left,right);
            case "*": return mul(left,right);
            case "/": return div(left,right);
        }
        return null;
    }

    public Object add( Object left, Object right ){
        if( left==null && right==null )return addNullNull();

        if( left instanceof String && right==null )return addStringNull((String) left);
        if( right instanceof String && left==null )return addNullString((String) right);
        if( left instanceof String && right instanceof String )return addStringString((String) left,(String) right);

        if( left instanceof Double && right==null )return addDoubleNull((Double) left);
        if( right instanceof Double && left==null )return addNullDouble((Double) right);
        if( left instanceof Double && right instanceof Double )return addDoubleDouble((Double) left,(Double) right);

        throw new Error("can't add left="+left+" right="+right);
    }
    public Object addNullNull(){ return null; }
    public Object addStringNull(String str){ return str+null; }
    public Object addNullString(String str){ return null+str; }
    public Object addStringString(String a,String b){ return a+b; }
    public Object addDoubleNull(Double d){ return null; }
    public Object addNullDouble(Double d){ return null; }
    public Object addDoubleDouble(Double a,Double b){ return a+b; }

    public Object sub( Object left, Object right ){
        if( left==null && right==null )return subNullNull();

        if( left instanceof String && right==null )return subStringNull((String) left);
        if( right instanceof String && left==null )return subNullString((String) right);
        if( left instanceof String && right instanceof String )return subStringString((String) left,(String) right);

        if( left instanceof Double && right==null )return subDoubleNull((Double) left);
        if( right instanceof Double && left==null )return subNullDouble((Double) right);
        if( left instanceof Double && right instanceof Double )return subDoubleDouble((Double) left,(Double) right);

        throw new Error("can't sub left="+left+" right="+right);
    }
    public Object subNullNull(){ return null; }
    public Object subStringNull(String str){ throw new Error("can't string - null"); }
    public Object subNullString(String str){ return new Error("can't string - null"); }
    public Object subStringString(String a,String b){ return a.replace(b,""); }
    public Object subDoubleNull(Double d){ return null; }
    public Object subNullDouble(Double d){ return null; }
    public Object subDoubleDouble(Double a,Double b){ return a-b; }

    public Object mul( Object left, Object right ){
        return null;
    }
    public Object div( Object left, Object right ){
        return null;
    }
}
