package xyz.cofe.lang.basic.inter;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.junit.jupiter.api.Test;
import xyz.cofe.lang.basic.BasicBaseVisitor;
import xyz.cofe.lang.basic.BasicLexer;
import xyz.cofe.lang.basic.BasicParser;

public class BasicTest {
    @Test
    public void test01(){
        var lex = new BasicLexer(CharStreams.fromString("1 + 2"));
        var parser = new BasicParser(new CommonTokenStream(lex));

        var r = parser.expr();
        System.out.println(r.getStart());
        System.out.println(r.getStop());

        r.accept(new BasicBaseVisitor<>(){
            @Override
            public Object visitChildren(RuleNode node) {
                int level = 0;
                var n = (ParseTree)node;
                while (n.getParent()!=null){
                    level++;
                    n = n.getParent();
                }
                System.out.println(
                    ".|".repeat(level)+
                    "node : class="+node.getClass()+" text="+node.getText()
                );
                return super.visitChildren(node);
            }
        });

        System.out.println(new Interpetator().eval(r));
    }

    @Test
    public void vars01(){
        var lex = new BasicLexer(CharStreams.fromString("a + b"));
        var parser = new BasicParser(new CommonTokenStream(lex));

        var r = parser.expr();
        var iterpretator = new Interpetator();
        iterpretator.getVariables().put("a", 10);
        iterpretator.getVariables().put("b", 5.0);

        System.out.println(iterpretator.eval(r));
    }
}
