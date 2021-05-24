package xyz.cofe.jasm.lex;

import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.CharPointer;

public class KeyWordToken extends CToken {
    public final KeyWord keyWord;
    public KeyWordToken(CharPointer begin, CharPointer end, KeyWord keyWord) {
        super(begin, end);
        if( keyWord==null )throw new IllegalArgumentException( "keyWord==null" );
        this.keyWord = keyWord;
    }
}
