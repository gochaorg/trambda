package xyz.cofe.trambda.json;

import java.lang.invoke.SerializedLambda;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.Fn;
import xyz.cofe.trambda.bc.MethodDef;

public class MethodJsonTest {
    @Test
    public void test01(){
        System.out.println("test01");
        System.out.println("=".repeat(60));

        AtomicReference<MethodDef> mdefRef = new AtomicReference<>();

        IEnv env = new EnvLocal();

        AsmQuery<IEnv> query = new AsmQuery<IEnv>(){
            /**
             * Реализация вызова лямбды
             *
             * @param fn   лямбда
             * @param sl   лямбда - сериализация
             * @param mdef байт-код лямбды
             * @return результат вызова
             */
            @Override
            protected <RES> RES call(Fn<IEnv, RES> fn, SerializedLambda sl, MethodDef mdef){
                mdefRef.set(mdef);
                return super.call(fn, sl, mdef);
            }
        };

        var res = query.apply(
            env0 -> env0.getUsers().filter(u -> u.getName().contains("Petrov"))
        );

        System.out.println(MethodJson.toJson(mdefRef.get()));
    }
}
