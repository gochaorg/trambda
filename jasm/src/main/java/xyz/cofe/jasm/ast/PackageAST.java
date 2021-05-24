package xyz.cofe.jasm.ast;

import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;

public class PackageAST extends ASTBase<PackageAST> {
    public PackageAST(TPointer begin, TPointer end, String name){
        super(begin, end);
        this.name = name;
    }

    public PackageAST(PackageAST sample){
        super(sample);
    }

    protected String name;
    public String getName(){ return name; }
    public void setName(String name){
        this.name = name;
    }

    @Override
    public PackageAST clone(){
        return new PackageAST(this);
    }

    @Override
    public String toString(){
        return KeyWord.pkg.text+" "+name;
    }
}
