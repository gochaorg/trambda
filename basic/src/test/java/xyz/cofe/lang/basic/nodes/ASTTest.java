package xyz.cofe.lang.basic.nodes;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import xyz.cofe.lang.basic.BasicLexer;
import xyz.cofe.lang.basic.BasicParser;
import xyz.cofe.lang.basic.nodes.AST;

public class ASTTest {
    @Test
    public void test01(){
        var src = "fn add( a:int, b:int ):int { return a+b }";
        var lex = new BasicLexer(CharStreams.fromString(src));
        var parser = new BasicParser(new CommonTokenStream(lex));
        var root = parser.function();

        var tast = AST.wrap(root);
        tast.tree().forEach( ts -> {
            System.out.print(".|".repeat(ts.getLevel()+1));
            System.out.println(ts.getNode());
        });
    }
}
