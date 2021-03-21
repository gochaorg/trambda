package xyz.cofe.trambda.tcp;

import java.lang.invoke.SerializedLambda;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.Fn;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.tcp.demo.IEnv;
import xyz.cofe.trambda.tcp.demo.LinuxEnv;

public class LocalTest {
    @Test
    public void linuxEnvTest(){
        var localEnv = new LinuxEnv();
        //env.processes().forEach(System.out::println);

        AsmQuery<IEnv> query = new AsmQuery<>(){
            @Override
            protected <RES> RES call(Fn<IEnv, RES> fn, SerializedLambda sl, MethodDef mdef){
                return fn.apply(localEnv);
            }
        };

        var procs = query.apply( env ->
            env.processes().stream()
                .filter( p -> p.getName().matches("(?is).*(chrome|java).*") )
                .collect(Collectors.toList())
        );

        procs.forEach(System.out::println);
    }
}
