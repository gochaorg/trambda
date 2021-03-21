package xyz.cofe.trambda.tcp;

import org.junit.jupiter.api.Test;

public class SerializeTest {
    @Test
    public void test01(){
        byte[] data = Message.serialize(new Ping());
        Message msg = Message.deserialize(data);
        System.out.println(msg);
    }
}
