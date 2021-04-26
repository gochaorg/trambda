package xyz.cofe.trambda.l2;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class SerialLambdaTest {
    @Test
    public void serLambda01(){
        Fn<Fn<String,String>,String> test = (lambda) -> {
            System.out.println("lambda="+lambda.getClass());

            Method writeReplace = null;
            try{
                writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);

                SerializedLambda sl = (SerializedLambda) writeReplace.invoke(lambda);
                System.out.println(sl);
            } catch( NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
                e.printStackTrace();
            }

            return null;
        };
        test.apply( x -> x.repeat(4) );
    }
}
