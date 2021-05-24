package xyz.cofe.jasm.lex;

import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.CharPointer;

public class IdToken extends CToken {
    public IdToken(CharPointer begin, CharPointer end) {
        super(begin, end);
    }
}
