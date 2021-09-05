package xyz.cofe.trambda.tcp.ssl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.cofe.text.BytesDump;

import java.util.Random;

public class IntCodecTest {
    @Test
    public void test01(){
        int n = 10;
        var bytes = IntCodec.toBytes(n);
        var num = IntCodec.fromBytes(bytes);
        System.out.println(new BytesDump.Builder().build().dump(bytes));
        System.out.println(num);
        Assertions.assertTrue(num == n );

        n = 255;
        bytes = IntCodec.toBytes(n);
        num = IntCodec.fromBytes(bytes);
        System.out.println(new BytesDump.Builder().build().dump(bytes));
        System.out.println(num);
        Assertions.assertTrue(num == n );

        var rnd = new Random();
        for( int i=0; i<1000; i++ ) {
            n = rnd.nextInt();
            Assertions.assertTrue(IntCodec.fromBytes(IntCodec.toBytes(n)) == n);
        }
    }
}
