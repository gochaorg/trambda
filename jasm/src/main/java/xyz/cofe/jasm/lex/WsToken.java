package xyz.cofe.jasm.lex;

import java.util.List;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.CharPointer;

public class WsToken extends CToken {
    public WsToken(CharPointer begin, CharPointer end) {
        super(begin, end);
    }
    public WsToken(List<CToken> tokens) { super(tokens); }
}
