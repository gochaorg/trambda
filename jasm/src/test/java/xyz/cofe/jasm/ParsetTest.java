package xyz.cofe.jasm;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.cofe.jasm.ast.Parser;
import xyz.cofe.jasm.lex.Lexer;
import xyz.cofe.jasm.lex.WsToken;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.TPointer;

public class ParsetTest {
    @Test
    public void javaName(){
        List<? extends CToken> toks = Lexer.tokens( "java . name.i1" );
        toks.forEach(System.out::println);

        TPointer tptr = new TPointer(
            toks.stream().filter(t -> !(t instanceof WsToken)).collect(Collectors.toList())
        );

        var jn = Parser.javaName.apply(tptr);
        System.out.println(jn);
    }

    @Test
    public void unit(){
        var unit = Parser.unit.apply(
            new TPointer(
                Lexer.tokens(
                    "package my.pkg;\n" +
                        "\n" +
                        "public class MyClass {\n" +
                        "}\n"
                )
            )
        );

        Assertions.assertTrue(unit.isPresent());
        System.out.println(unit.get());
    }

    @Test
    public void typeName01(){
        var t = Parser.typeName.apply(
            new TPointer(
                Lexer.tokens(
                    "java.util.Coll<int,Map<Name>>"
                )
            )
        );
        System.out.println(t);
    }

    @Test
    public void typeName02(){
        var t = Parser.typeName.apply(
            new TPointer(
                Lexer.tokens(
                    "java.util.Coll<int,Name>"
                )
            )
        );
        System.out.println(t);
    }

    @Test
    public void fields(){
        var unit = Parser.unit.apply(
            new TPointer(
                Lexer.tokens(
                    "package my.pkg;\n" +
                        "\n" +
                        "public class MyClass {\n" +
                        "  public name1_b : String;\n" +
                        "  protected name1_c : int;\n" +
                        "}\n"
                )
            )
        );

        Assertions.assertTrue(unit.isPresent());
        System.out.println(unit.get());
    }

    @Test
    public void methods(){
        var unit = Parser.unit.apply(
            new TPointer(
                Lexer.tokens(
                    "package my.pkg;\n" +
                        "\n" +
                        "public class MyClass {\n" +
                        "  public meth1( a:int ) : String;\n" +
                        "}\n"
                )
            )
        );

        Assertions.assertTrue(unit.isPresent());
        System.out.println(unit.get());
    }

    @Test
    public void imports(){
        var unit = Parser.unit.apply(
            new TPointer(
                Lexer.tokens(
                    "package my.pkg;\n" +
                        "\n" +
                        "import java.lang.String;\n" +
                        "import java.lang.String as STR;\n" +
                        "\n" +
                        "public class MyClass {\n" +
                        "  public meth1( a:int ) : String;\n" +
                        "}\n"
                )
            )
        );

        Assertions.assertTrue(unit.isPresent());
        System.out.println(unit.get());
    }
}
