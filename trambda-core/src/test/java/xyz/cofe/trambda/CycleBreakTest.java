package xyz.cofe.trambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;
import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.sample.EnvLocal;
import xyz.cofe.trambda.sample.IEnv;

public class CycleBreakTest {
    public static void debug( String label ){}

    @Test
    public void test01(){
        //IEnv env = new EnvLocal();

        LambdaDump[] dump1 = new LambdaDump[]{ null };
        AsmQuery<IEnv> query = new AsmQuery<IEnv>(){
            @Override
            protected <RES> RES call(Fn1<IEnv, RES> fn, SerializedLambda sl, LambdaDump dump){
                dump1[0] = dump;
                return super.call(fn, sl, dump);
            }
        };

        query.apply( env -> {
            for( int i=0; i<100; i++ ){
                debug("0");
                System.out.println("cycle "+i);
                debug("1");
            }
            return null;
        });

        var dump = dump1[0];
        var classByteCode = dump.restore().classByteCode();
        LambdaDump.dump(System.out::println,classByteCode);

        var meth = dump.restore().method();
        try{
            meth.invoke(null, new Object[]{null});
        } catch( IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
}
