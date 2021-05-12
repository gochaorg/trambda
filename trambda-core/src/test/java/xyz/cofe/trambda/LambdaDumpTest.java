package xyz.cofe.trambda;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.junit.jupiter.api.Test;
import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.sample.EnvLocal;
import xyz.cofe.trambda.sample.IEnv;
import xyz.cofe.trambda.sample.User;

public class LambdaDumpTest {
    @Test
    public void test01(){
        var dump = new LambdaDump().dump( (String string) -> string );
        System.out.println(dump);

        var bytes = Serializer.toBytes(dump);
        System.out.println("bytes "+bytes.length);

        dump = Serializer.fromBytes(bytes);
        System.out.println("node");
        System.out.println(dump.getLambdaNode());
    }

    @Test
    public void test02(){
        IEnv localEnv = new EnvLocal();
        Fn1<IEnv, List<User>> fetch = env -> env.getUsers().filter(u -> u.getName().contains("Pet") ).toList();
        fetch.apply(localEnv).forEach( u -> {
            System.out.println("user "+u.getName());
        });

        var dump = new LambdaDump().dump(fetch);
        var bytes = Serializer.toBytes(dump);
        dump = Serializer.fromBytes(bytes);
        System.out.println("node");
        System.out.println(dump.getLambdaNode());

        System.out.println("= ".repeat(40));
        System.out.println("try restore method");

        var meth = dump.restore().method();
        try{
            System.out.println("try call");
            Object obj = meth.invoke(null, localEnv);
            if( obj instanceof List ){
                ((List)obj).forEach( u -> {
                    System.out.println(u);
                });
            }else{
                System.out.println("not list");
            }
        } catch( IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
}
