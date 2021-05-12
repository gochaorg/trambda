package xyz.cofe.trambda;

import java.util.function.Function;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.sample.EnvLocal;
import xyz.cofe.trambda.sample.IEnv;

public class SampleCallTest {
    @Test
    public void test02(){
        IEnv env = new EnvLocal();

        AsmQuery<IEnv> query = new AsmQuery<IEnv>();

        for( int i=0;i<10;i++ ){
            var res =
                query.apply(
                    env0 -> env0.getUsers().filter(u -> u.getName().contains("Petrov"))
                );
        }
    }

    @Test
    public void test03(){
        Function<Function<String,String>,String> test = (f) -> {
            System.out.println("f="+f.getClass());
            return null;
        };
        test.apply( x -> x.repeat(4) );
    }
}
