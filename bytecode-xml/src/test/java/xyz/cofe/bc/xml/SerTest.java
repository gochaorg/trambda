package xyz.cofe.bc.xml;

import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.bc.cls.CBegin;

public class SerTest {
    @Test
    public void test01(){
        System.out.println("hello");

        var javaCN = CBegin.parseByteCode(SerTest.class.getResource("/cls/JavaClassName.class"));
        BCSeriliazer ser = new BCSeriliazer();
        //ser.writeAcessFlagHelp();
        ser.write(javaCN);
    }
}
