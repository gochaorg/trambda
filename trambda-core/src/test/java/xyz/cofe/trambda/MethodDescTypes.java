package xyz.cofe.trambda;

import java.util.ArrayList;

public class MethodDescTypes {
    public final TypeDesc[] params;
    public final TypeDesc returns;

    public MethodDescTypes(TypeDesc[] params, TypeDesc returns){
        if( params == null ) throw new IllegalArgumentException("params==null");
        if( returns == null ) throw new IllegalArgumentException("returns==null");
        this.params = params;
        this.returns = returns;
    }
    public static MethodDescTypes parse( String desc ){
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        if( !desc.startsWith("(") )throw new IllegalArgumentException("desc not start with '('");
        if( desc.length()<2 )throw new IllegalArgumentException("desc length < 2");

        var params = new ArrayList<TypeDesc>();
        var ptr = 1;
        while( true ){
            var p = TypeDesc.parse(desc, ptr);
            if( p.to<=ptr ){
                throw new IllegalStateException("!!");
            }
            params.add(p.type);
            ptr = p.to;
            if( ptr>=desc.length() )break;
            if( desc.charAt(ptr)==')' ){
                break;
            }
        }

        TypeDesc ret = null;

        ptr++;
        if( ptr<desc.length() ){
            var p = TypeDesc.parse(desc,ptr);
            ret = p.type;
        }else{
            throw new IllegalStateException("can't parse return type");
        }

        return new MethodDescTypes(params.toArray(new TypeDesc[0]), ret);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int pi = -1;
        for( var p : params ){
            pi++;
            if( pi>0 ){
                sb.append(", ");
            }
            sb.append(p);
        }
        sb.append(")");
        sb.append(" ").append(returns);
        return sb.toString();
    }
}
