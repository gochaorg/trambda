package xyz.cofe.trambda;

import java.lang.invoke.SerializedLambda;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sec.MethodDescTypes;
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.SecurFilters;

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

        var sfilters = SecurFilters.create()
            .allow(a -> {
                a.call( call -> call.getOwner().equals("java.lang.invoke.StringConcatFactory"), "Java compiler" );
                a.call( call -> call.getOwner().equals("java.lang.invoke.LambdaMetafactory"), "Java compiler" );
            })
            .allow(a -> {
                a.field( f -> f.getOwner().equals("java.lang.System") && f.isReadAccess(), "System stdio" );
                a.call( f -> f.getOwner().matches("java\\.io\\.[\\w\\d]*(Stream|Writer)[\\w\\d]*"), "Java Streams" );
                a.call( c -> c.getOwner().matches("java.lang.String"), "api Java lang" );
            })
            .deny(b -> {
                b.field(f -> f.getOwner().equals("java.lang.System") && f.isWriteAccess(), "deny System field write");
                b.call(f -> f.getOwner().equals("java.lang.System") && f.getMethodName().matches(
                    "(?i)gc|exit|console|clear.*|getSecurity.*|inherited.*|load.*|map.*|run.*|set.*|wait.*"), "deny call System method");
            })
            .allow( a -> {
                a.call( c -> c.getOwner().matches("xyz.cofe.iter.[\\w\\d]+"), "api by xyz.cofe" );
                a.call( c -> c.getOwner().matches("xyz.cofe.[\\w\\d]+"), "api by xyz.cofe" );
                a.call( c -> c.getOwner().matches("xyz.cofe.trambda.[\\w\\d]+"), "api by trambda" );
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
