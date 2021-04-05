package xyz.cofe.trambda;

import java.lang.invoke.SerializedLambda;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.bc.MethodDef;

public class SecurTest {
    @Test
    public void test00(){
        var desc = "(Ljava/lang/String;Lxyz/cofe/trambda/IEnv;)Lxyz/cofe/iter/Eterable;";
        var mdesc = MethodDescTypes.parse(desc);
        System.out.println(mdesc);
    }

    @Test
    public void test01(){
        System.out.println("test01");
        System.out.println("=".repeat(60));

        AtomicReference<MethodDef> mdefRef = new AtomicReference<>();

        IEnv env = new EnvLocal();
        AsmQuery<IEnv> query = new AsmQuery<IEnv>(){
            @Override
            protected <RES> RES call(Fn<IEnv, RES> fn, SerializedLambda sl, MethodDef mdef){
                mdefRef.set(mdef);
                return super.call(fn, sl, mdef);
            }
        };

        var str = "Petrov";
        var str2 = "";
        var res =
            query.apply(
                env0 -> env0.getUsers().filter(u -> u.getName().contains(str+str2))
            );

        System.out.println("-".repeat(60));
        var mdef = mdefRef.get();
        System.out.println(MethodDescTypes.parse(mdef.getDescriptor()));


    }
}
