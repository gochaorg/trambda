package xyz.cofe.jasm.ast;

import xyz.cofe.jasm.lex.IdToken;
import xyz.cofe.text.tparse.TPointer;

public class IdTokenAST extends AbstractKeywordAST<IdToken> {
    public IdTokenAST(IdTokenAST sample) {
        super(sample);
    }

    public IdTokenAST(TPointer begin, IdToken keywordTok){
        super(begin, keywordTok);
    }

    public IdTokenAST clone(){ return new IdTokenAST(this); }
}
