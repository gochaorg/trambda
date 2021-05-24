package xyz.cofe.jasm;

import org.junit.jupiter.api.Test;
import xyz.cofe.jasm.lex.Lexer;

public class LexerTest {
    @Test
    public void test01(){
        Lexer.tokenizer("class 12 # abc \n" +
            " tyya " +
            "xyz:" +
            "yui { }[]()")
            .forEach(System.out::println);
    }
}
