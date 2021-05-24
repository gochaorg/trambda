package xyz.cofe.jasm.ast;

import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;

public class ImportAST extends ASTBase<ImportAST> {
    public ImportAST(TPointer begin, TPointer end){
        super(begin, end);
    }

    public ImportAST(ImportAST sample){
        super(sample);
        if( sample!=null ){
            if( sample.type!=null ){
                type = sample.type.clone();
            }
            if( sample.alias!=null ){
                alias = sample.alias.clone();
            }
        }
    }

    protected TypeNameAST type;
    public TypeNameAST getType(){ return type; }
    public void setType(TypeNameAST type){ this.type = type; }

    protected TypeNameAST alias;
    public TypeNameAST getAlias(){ return alias; }
    public void setAlias(TypeNameAST alias){ this.alias = alias; }

    @Override
    public ImportAST clone(){
        return new ImportAST(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(KeyWord._import.text).append(" ").append(type);
        if( alias!=null ){
            sb.append(" ").append(KeyWord.as.text).append(" ");
            sb.append(alias);
        }
        sb.append(KeyWord.semicolon.text);
        return sb.toString();
    }
}
