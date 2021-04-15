package xyz.cofe.trambda.demo.api;

import org.junit.jupiter.api.Test;

public class LinuxEnvTest {
    @Test
    public void test(){
        var env = new LinuxEnv();
        env.processes().stream()
            .filter(p->p.getName().equalsIgnoreCase("java"))
            .forEach(System.out::println);
    }
}
