package xyz.cofe.jasm.lex;

import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.CharPointer;

public class CommentToken extends CToken {
    public CommentToken(CharPointer begin, CharPointer end) {
        super(begin, end);
    }
}
