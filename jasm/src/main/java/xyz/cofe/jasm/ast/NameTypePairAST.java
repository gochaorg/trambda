package xyz.cofe.jasm.ast;

import java.util.function.Consumer;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;

public class NameTypePairAST extends ASTBase<NameTypePairAST> {
    public NameTypePairAST(TPointer begin, TPointer end){
        super(begin,end);
    }

    public NameTypePairAST(NameTypePairAST sample){
        super(sample);
        if( sample!=null ){
            name = sample.name;
            if( sample.type!=null ){
                type = sample.type.clone();
            }
        }
    }

    public NameTypePairAST configure(Consumer<NameTypePairAST> conf){
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

    @Override
    public NameTypePairAST clone(){
        return new NameTypePairAST(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name)
            //.append(" ")
            .append(KeyWord.colon.text)
            //.append(" ")
            .append(type);
        return sb.toString();
    }
}
