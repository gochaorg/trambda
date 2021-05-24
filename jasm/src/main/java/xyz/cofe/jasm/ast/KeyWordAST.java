package xyz.cofe.jasm.ast;

import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;

public class KeyWordAST extends AbstractKeywordAST<KeyWord> {
    public KeyWordAST(KeyWordAST sample) {
        super(sample);
        if( sample!=null ){
            this.token = sample.token;
        }
    }

    public KeyWordAST(TPointer begin, KeyWord keywordTok){
        super(begin, keywordTok);
    }

    public KeyWordAST clone(){ return new KeyWordAST(this); }
}
