package xyz.cofe.lang.basic.inter;

import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import xyz.cofe.lang.basic.BasicParser.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Простой интерпретатор
 */
public class Interpetator {
    public Object eval(RuleNode node){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( node instanceof LiteralContext ) return eval((LiteralContext) node);
        if( node instanceof AtomContext ) return eval( ((AtomContext)node) );
        if( node instanceof AtomValueContext ) return eval( ((AtomValueContext)node).atom() );
        if( node instanceof ParenthesesContext ) return eval( ((ParenthesesContext)node).expr() );
        if( node instanceof UnaryOpContext ) return eval( (UnaryOpContext)node );
        if( node instanceof BinOpContext ) return eval( (BinOpContext)node );
        if( node instanceof RContext ) return eval( ((RContext)node).expr() );
        if( node instanceof VarRefContext ) return eval( ((VarRefContext)node) );
        return null;
    }

    public Object eval( AtomContext atom ){
        if( atom.literal()!=null )return eval(atom.literal());
        if( atom.varRef()!=null )return eval(atom.varRef());
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

    protected Map<String,Object> variables = new LinkedHashMap<>();
    public Map<String,Object> getVariables(){ return variables; }
    public void setVariables(Map<String,Object> vars){
        this.variables = vars;
    }

    public Object eval( VarRefContext varRef ){
        var vars = getVariables();
        if( vars==null )return null;
        return vars.get(varRef.ID().getText());
    }
    public Object eval( BinOpContext node ){
        if( node==null )throw new IllegalArgumentException( "node==null" );
        var op = node.op!=null ? node.op.getText() : "";
        var left = eval(node.left);
        var right = eval(node.right);
        switch (op){
            case "+": return binOp("add", left,right);
            case "-": return binOp("sub", left,right);
            case "*": return binOp("mul", left,right);
            case "/": return binOp("div", left,right);
        }
        return null;
    }

    protected Operators operators = new Operators();
    protected Map<String,Operators.Target> binOpCache = new HashMap<>();
    protected Object binOp( String name, Object left, Object right ){
        Class leftType = left==null ? NullRef.class : left.getClass();
        Class rightType = right==null ? NullRef.class : right.getClass();
        String id = name+":"+leftType+":"+rightType;
        Operators.Target trgt = binOpCache.get(id);
        if( trgt!=null ){
            return trgt.invoke(left,right);
        }

        var fnd = operators.find("add",leftType,rightType).preffered();
        if( fnd.isEmpty() ){
            throw new Error("can't add left="+leftType+" right="+rightType+" implementation not found");
        }

        trgt = fnd.get();
        binOpCache.put(id,trgt);

        return trgt.invoke(left,right);
    }
}
