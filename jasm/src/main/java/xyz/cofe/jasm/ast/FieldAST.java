package xyz.cofe.jasm.ast;

import java.util.function.Consumer;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;

public class FieldAST extends ASTBase<FieldAST> {
    public FieldAST(TPointer begin, TPointer end){
        super(begin,end);
    }

    public FieldAST(FieldAST sample){
        super(sample);
        if( sample!=null ){
            name = sample.name;
            if( sample.type!=null ){
                type = sample.type.clone();
            }
            access = sample.access;
        }
    }

    public FieldAST configure(Consumer<FieldAST> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    protected TypeNameAST type;
    public TypeNameAST getType(){
        return type;
    }
    public void setType(TypeNameAST type){
        this.type = type;
    }

    protected int access;
    public int getAccess(){ return access; }
    public void setAccess(int access){ this.access = access; }

    @Override
    public FieldAST clone(){
        return new FieldAST(this);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if( access!=0 ){
            for( var kw : KeyWord.values() ){
                if( kw.accFlag.isPresent() ){
                    int bit = kw.accFlag.get();
                    var isSet = (access & bit) == bit;
                    if( isSet ){
                        sb.append(kw.text);
                        sb.append(" ");
                    }
                }
            }
        }
//        sb.append(KeyWord._var.text);
        sb.append(name).append(" ").append(KeyWord.colon.text).append(" ").append(type).append(KeyWord.semicolon.text);
        return sb.toString();
    }
}
