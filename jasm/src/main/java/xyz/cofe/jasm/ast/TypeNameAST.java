package xyz.cofe.jasm.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;

public class TypeNameAST extends ASTBase<TypeNameAST> {
    public TypeNameAST(TypeNameAST sample) {
        super(sample);
        if( sample!=null ){
            name = sample.name;
            if( sample.params!=null ){
                params = new ArrayList<>();
                for( var p : sample.params ){
                    params.add( p!=null ? p.clone() : p );
                }
            }
        }
    }

    public TypeNameAST(TPointer begin, TPointer end){
        super(begin, end);
    }

    public TypeNameAST configure(Consumer<TypeNameAST> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    protected String name;
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    protected List<TypeNameAST> params;
    public List<TypeNameAST> getParams(){
        if( params==null )params = new ArrayList<>();
        return params;
    }
    public void setParams(List<TypeNameAST> params){
        this.params = params;
    }

    public TypeNameAST clone(){ return new TypeNameAST(this); }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if( params!=null && !params.isEmpty() ){
            sb.append("<");
            int i = -1;
            for( var p : params ){
                i++;
                if( i>0 )sb.append(",");
                if( p==null ){
                    sb.append("null");
                }else{
                    sb.append(p.toString());
                }
            }
            sb.append(">");
        }
        return sb.toString();
    }

    public boolean isPrimitive(){
        String name = toString();
        for( var k : KeyWord.values() ){
            if( name.equals(k.text) && k.primitive ){
                return true;
            }
        }
        return false;
    }
}
