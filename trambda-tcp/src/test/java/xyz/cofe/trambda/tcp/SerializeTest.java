package xyz.cofe.trambda.tcp;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SerializeTest {
    @Tag(T.Auto)
    @Test
    public void test01(){
        byte[] data = Serializer.toBytes(new Ping());
        Message msg = Serializer.fromBytes(data);
        System.out.println(msg);
    }
}
