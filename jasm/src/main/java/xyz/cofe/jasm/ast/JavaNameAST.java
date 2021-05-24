package xyz.cofe.jasm.ast;

import xyz.cofe.text.tparse.TPointer;

public class JavaNameAST extends ASTBase<JavaNameAST> {
    public JavaNameAST(TPointer begin, TPointer end){
        super(begin, end);
    }
    public JavaNameAST(JavaNameAST sample){
        super(sample);
        if( sample!=null ){
            this.name = sample.name;
        }
    }

    protected String name;
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    @Override
    public JavaNameAST clone(){
        return new JavaNameAST(this);
    }

    @Override
    public String toString(){
        return "JavaNameAST "+name;
    }
}
