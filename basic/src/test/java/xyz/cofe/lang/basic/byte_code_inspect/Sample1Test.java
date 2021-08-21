package xyz.cofe.lang.basic.byte_code_inspect;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import xyz.cofe.bc.xml.BCSeriliazer;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CMethod;

import java.util.function.Predicate;

public class Sample1Test {
    public static int sum( int a, int b ){
        return a + b;
    }

    public static double sum2( Double a, Double b ){
        return a + b;
    }

    public static int sum3( byte a, short b ){ return a+b; }

    public static long cycle1(int ...a){
        long r = 0;
        for( int i=0; i<a.length; i++ ){
            r += a[i];
        }
        for( short i=(short)(a.length-1); i>=0; i-- ){
            r += ((long) a[i]) *a[i];
        }
        return r;
    }

    public static void dump(String title,CBegin cBegin, Predicate<CMethod> meth){
        var sumMethod = cBegin.getMethods().stream().filter(meth).findFirst();
        assertTrue(sumMethod.isPresent());

        System.out.println(Text.align(" "+title+" ", Align.Center,"-",80));
        new BCSeriliazer().write(sumMethod.get());
    }

    @Test
    public void inspectSum(){
        System.out.println("inspectSum");
        System.out.println("=".repeat(80));

        System.out.println("parse byte code");
        var cbegin = CBegin.parseByteCode(Sample1Test.class);

        dump("sum() byte code", cbegin, m -> m.getName().equals("sum") );
        dump("sum2() byte code", cbegin, m -> m.getName().equals("sum2") );
        dump("sum3() byte code", cbegin, m -> m.getName().equals("sum3") );
        dump("cycle1() byte code", cbegin, m -> m.getName().equals("cycle1") );
    }

    @Test
    public void defaultClass(){
        System.out.println("defaultClass");
        System.out.println("=".repeat(80));

        var cbegin = CBegin.parseByteCode(Sample1Test.class);
        System.out.println(cbegin);

        cbegin.getMethods().forEach(System.out::println);
    }
}
