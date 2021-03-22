package xyz.cofe.trambda.tcp;

import org.junit.jupiter.api.Test;

public class SerializeTest {
    @Test
    public void test01(){
        byte[] data = Serializer.toBytes(new Ping());
        Message msg = Serializer.fromBytes(data);
        System.out.println(msg);
    }
}
