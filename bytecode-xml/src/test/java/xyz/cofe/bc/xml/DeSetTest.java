package xyz.cofe.bc.xml;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CSource;

public class DeSetTest {
    @Test
    public void test01(){
        BCDeserializer derser = new BCDeserializer();
        String xml = null;
        try{
            xml = IOFun.readText(DeSetTest.class.getResource("/xml/JavaClassName.xml"), "utf-8");
        } catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        var obj = derser.restore(xml);

        if( obj instanceof CBegin ){
            new BCSeriliazer().write((CBegin) obj);
        }
    }
}
