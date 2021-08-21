package xyz.cofe.lang.basic.byte_code_inspect;

import org.junit.jupiter.api.Test;
import xyz.cofe.bc.xml.BCSeriliazer;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CMethod;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xyz.cofe.lang.basic.byte_code_inspect.Sample1Test.dump;

public class StringTest {
    public static int len1( String str ){
        return str.length();
    }

    public static String substr1( String str, int a, int b ){
        return str.substring(a,b);
    }

    public static String concat2( String str, String str2 ){
        return str + str2;
    }

    public static String concat3( String str, String str2, String str3 ){
        return str + str2 + "a" + str3;
    }

    @Test
    public void inspectString(){
        System.out.println("inspectString");
        System.out.println("=".repeat(80));

        System.out.println("parse byte code");
        var cbegin = CBegin.parseByteCode(StringTest.class);

        dump("len1() byte code", cbegin, m -> m.getName().equals("len1") );
        dump("substr1() byte code", cbegin, m -> m.getName().equals("substr1") );
        dump("concat1() byte code", cbegin, m -> m.getName().equals("concat2") );
    }

    @Test
    public void dumpAll(){
        System.out.println("dumpAll");
        System.out.println("=".repeat(80));

        System.out.println("parse byte code");
        var cbegin = CBegin.parseByteCode(StringTest.class);

        new BCSeriliazer().write(cbegin);
    }
}
