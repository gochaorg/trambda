package xyz.cofe.lang.basic.nodes;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.cofe.lang.basic.BasicLexer;
import xyz.cofe.lang.basic.BasicParser;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("SimplifiableAssertion")
public class ToasterTest {
    public FunAST funAST(String source){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        return AST.wrap(
            new BasicParser(new CommonTokenStream(
                new BasicLexer(CharStreams.fromString(source)))
            ).function()
        );
    }

    public void dump(AST<?,?> ast){
        ast.tree().forEach( ts -> {
            System.out.print(".|".repeat(ts.getLevel()+1));
            System.out.println(" "+ts.getNode());
        });
    }

    public void dump(String src,AST<?,?> ast){
        System.out.println(src);
        dump(ast);
    }

    @Test
    public void test01(){
        var src = "fn add( a:int, b:int ):int { return a+b }";
        var ast = funAST(src);

        // resolve fun types
        System.out.println("before funTypes()");
        dump(src,ast);

        var match = new AtomicInteger(0);

        var toaster = new Toaster(){
            @Override
            protected void ok(AST<?, ?> ast, String message) {
                match.incrementAndGet();
                super.ok(ast, message);
            }
        };

        toaster.resolve(ast);
        System.out.println("after resolve()");
        dump(ast);
    }

    @Test
    public void test01b(){
        var src = "fn add( a:int, b:intz ):int { return a+b }";
        var ast = funAST(src);

        System.out.println("before");
        dump(src,ast);

        var match = new AtomicInteger(0);

        var toaster = new Toaster(){
            @Override
            protected void error(AST<?, ?> ast, String message) {
            match.incrementAndGet();
            super.error(ast, message);
            }
        };

        toaster.funTypes(ast);
        System.out.println("after");
        dump(ast);

        Assertions.assertTrue(match.get()>0);
    }
}
