package xyz.cofe.trambda;

import java.lang.invoke.SerializedLambda;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sample.EnvLocal;
import xyz.cofe.trambda.sample.IEnv;
import xyz.cofe.trambda.sec.MethodDescTypes;
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.SecurityFilters;

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
                env0 -> env0.getUsers().filter(u -> {
                    System.out.println("bla bla");
                    System.currentTimeMillis();
                    System.load("dll1");
                    return u.getName().contains(str+str2);
                })
            );

        System.out.println("-".repeat(60));
        var mdef = mdefRef.get();
        System.out.println(MethodDescTypes.parse(mdef.getDescriptor()));

        System.out.println("- ".repeat(30));

        var secAcc = SecurAccess.inspect(mdef);
        secAcc.forEach(System.out::println);

        var sfilters = SecurityFilters.create()
            .allow(a -> {
                a.invoke("Java compiler", call -> call.getOwner().equals("java.lang.invoke.StringConcatFactory"));
                a.invoke("Java compiler", call -> call.getOwner().equals("java.lang.invoke.LambdaMetafactory"));
            })
            .allow(a -> {
                a.field("System stdio", f -> f.getOwner().equals("java.lang.System") && f.isReadAccess());
                a.invoke("Java Streams", f -> f.getOwner().matches("java\\.io\\.[\\w\\d]*(Stream|Writer)[\\w\\d]*"));
                a.invoke("api Java lang", c -> c.getOwner().matches("java.lang.String"));
            })
            .deny(b -> {
                b.field("deny System field write", f -> f.getOwner().equals("java.lang.System") && f.isWriteAccess());
                b.invoke("deny call System method", f -> f.getOwner().equals("java.lang.System") && f.getMethodName().matches(
                    "(?i)gc|exit|console|clear.*|getSecurity.*|inherited.*|load.*|map.*|run.*|set.*|wait.*"));
            })
            .allow( a -> {
                a.invoke("api by xyz.cofe", c -> c.getOwner().matches("xyz.cofe.iter.[\\w\\d]+"));
                a.invoke("api by xyz.cofe", c -> c.getOwner().matches("xyz.cofe.[\\w\\d]+"));
                a.invoke("api by trambda", c -> c.getOwner().matches("xyz.cofe.trambda.[\\w\\d]+"));
            })
            .deny().any("Deny by default")
            .build();

        System.out.println("# ".repeat(40));
        sfilters.validate(secAcc).forEach(sm -> System.out.println(
            "allow="+sm.isAllow()+
                " message="+sm.getMessage()+
                " access="+sm.getAccess()));
    }
}
