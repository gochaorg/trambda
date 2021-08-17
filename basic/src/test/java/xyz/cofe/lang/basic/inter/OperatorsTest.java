package xyz.cofe.lang.basic.inter;

import org.junit.jupiter.api.Test;

public class OperatorsTest {
    @Test
    public void test01(){
        Operators op = new Operators();
        op.functions.forEach(System.out::println);
    }
}
