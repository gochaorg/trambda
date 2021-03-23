package xyz.cofe.trambda.demo.client;

import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.demo.api.IEnv;
import xyz.cofe.trambda.tcp.TcpQuery;

public class ClientTest {
    @Test
    public void test01(){
        var query = TcpQuery
            .create(IEnv.class).host("localhost").port(9988)
            .build();

        query.apply(
            env -> env.processes().stream().filter(p -> p.getName().contains("java"))
            .collect(Collectors.toList())
        ).forEach(System.out::println);
    }
}
