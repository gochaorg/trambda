package xyz.cofe.jasm.ast;

import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.TPointer;

public class CTokenAST extends AbstractKeywordAST<CToken> {
    public CTokenAST(CTokenAST sample) {
        super(sample);
        if( sample!=null ){
            this.token = sample.token;
        }
    }

    public CTokenAST(TPointer begin, CToken keywordTok){
        super(begin, keywordTok);
    }

    public CTokenAST clone(){ return new CTokenAST(this); }
}
