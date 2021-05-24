package xyz.cofe.jasm.ast;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.TPointer;

public class MethodAST extends ASTBase<MethodAST> {
    public MethodAST(TPointer begin, TPointer end){
        super(begin,end);
    }
    public MethodAST(MethodAST sample){
        super(sample);
        if( sample!=null ){
            name = sample.name;
            if( sample.returnType !=null ) returnType = sample.returnType.clone();
            if( sample.args!=null )args = new ArrayList<>(sample.args);
            if( sample.exceptions!=null )exceptions = new ArrayList<>(sample.exceptions);
            access = sample.access;
        }
    }

    //region name : String
    protected String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region returnType : TypeNameAST
    protected TypeNameAST returnType;
    public TypeNameAST getReturnType(){
        return returnType;
    }
    public void setReturnType(TypeNameAST returnType){
        this.returnType = returnType;
    }
    //endregion
    //region args : List<NameTypePairAST>
    protected List<NameTypePairAST> args;
    public List<NameTypePairAST> getArgs(){
        if( args==null )args = new ArrayList<>();
        return args;
    }
    public void setArgs(List<NameTypePairAST> args){
        this.args = args;
    }
    //endregion
    //region exceptions : List<NameTypePairAST>
    protected List<TypeNameAST> exceptions;
    public List<TypeNameAST> getExceptions(){
        if( exceptions==null )exceptions = new ArrayList<>();
        return exceptions;
    }
    public void setExceptions(List<TypeNameAST> exceptions){
        this.exceptions = exceptions;
    }
    //endregion
    //region access : int
    protected int access;
    public int getAccess(){ return access; }
    public void setAccess(int access){ this.access = access; }
    //endregion
    //region body : List<? extends CToken>
    protected List<CToken> body;
    public List<CToken> getBody(){
        if( body!=null )body = new ArrayList<>();
        return body;
    }
    public void setBody(List<CToken> body){
        this.body = body;
    }
    //endregion

    @Override
    public MethodAST clone(){
        return new MethodAST(this);
    }

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

        sb.append(name);
        sb.append(KeyWord.parenthesisOpen.text).append(" ");
        if( args!=null && args.size()>0 ){
            int i = -1;
            for( var a : args ){
                i++;
                if( i>0 )sb.append(KeyWord.comma.text).append(" ");
                sb.append(a);
            }
        }
        sb.append(" ").append(KeyWord.parenthesisClose.text)
            .append(" ")
            .append(KeyWord.colon.text)
            .append(" ")
            .append(returnType);

        if( exceptions!=null && exceptions.size()>0 ){
            sb.append(" ").append(KeyWord._throws.text).append(" ");
            int i = -1;
            for( var e : exceptions ){
                i++;
                if( i>0 )sb.append(KeyWord.comma.text).append(" ");
                sb.append(e);
            }
        }

        sb.append(KeyWord.semicolon.text);

        return sb.toString();
    }
}
