package xyz.cofe.jasm.ast;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.TPointer;

public class MethodBodyAST extends ASTBase<MethodBodyAST> {
    public MethodBodyAST(TPointer begin, TPointer end){
        super(begin, end);
    }
    public MethodBodyAST(MethodBodyAST sample){
        super(sample);
        if( sample!=null ){
            if( sample.body!=null ){
                body = new ArrayList<>(sample.body);
            }
        }
    }

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
    public MethodBodyAST clone(){
        return new MethodBodyAST(this);
    }
}
