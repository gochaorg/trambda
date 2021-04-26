package xyz.cofe.trambda.l1;

import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class SimpleLambdaTest {
    @Test
    public void javaLambda01(){
        Function<Function<String,String>,String> test = (f) -> {
            System.out.println("f="+f.getClass());
            return null;
        };
        test.apply( x -> x.repeat(4) );
    }
}
