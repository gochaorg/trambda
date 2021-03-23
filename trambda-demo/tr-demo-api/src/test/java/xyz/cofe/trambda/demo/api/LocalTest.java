package xyz.cofe.trambda.demo.api;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.Fn;
import xyz.cofe.trambda.MethodRestore;
import xyz.cofe.trambda.Query;
import xyz.cofe.trambda.bc.MethodDef;

public class LocalTest {
    private IEnv envInst;
    private synchronized IEnv env(){
        if( envInst!=null )return envInst;
        envInst = new LinuxEnv();
        return envInst;
    }

    private AsmQuery<IEnv> queryInst;
    private final Map<Fn<?,?>, Method> implMethod = new ConcurrentHashMap<>();

    public synchronized Query<IEnv> query(){
        if( queryInst!=null )return queryInst;

        queryInst = new AsmQuery<>(){
            @Override
            protected <RES> RES call(Fn<IEnv, RES> fn, SerializedLambda sl, MethodDef mdef){
                var meth = implMethod.computeIfAbsent(fn, x -> {
                    return compile(mdef);
                });

                if( meth!=null ){
                    try{
                        var res = meth.invoke(null,env());
                        //noinspection unchecked
                        return (RES)res;
                    } catch( IllegalAccessException | InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
                return fn.apply(env());
            }
        };

        return queryInst;
    }

    private Method compile(MethodDef mdef){
        var clName = LocalTest.class.getName().toLowerCase()+".Build1";
        var methName = "lambda1";

        System.out.println("generate bytecode "+clName+" method "+methName);
        var byteCode = new MethodRestore().className(clName).methodName(methName).methodDef(mdef).generate();

        System.out.println("create classloader");
        ClassLoader cl = new ClassLoader(ClassLoader.getSystemClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException{
                if( name!=null && name.equals(clName) ){
                    return defineClass(name,byteCode,0,byteCode.length);
                }
                return super.findClass(name);
            }
        };

        System.out.println("try load class "+clName);
        Class c = null;
        try{
            c = Class.forName(clName,true,cl);
            System.out.println("class found "+c);
        } catch( ClassNotFoundException e ) {
            e.printStackTrace();
            return null;
        }

        Method m = null;
        System.out.println("find method "+methName);
        for( var delMeth : c.getDeclaredMethods() ){
            System.out.println(""+delMeth);
            if( delMeth.getName().equals(methName) ){
                m = delMeth;
                break;
            }
        }

        return m;
    }

    @Test
    public void linuxEnvTest(){
        var procs = query().apply( env ->
            env.processes().stream()
                .filter( p -> p.getName().matches("(?is).*(chrome|java).*") )
                .collect(Collectors.toList())
        );

        procs.forEach(System.out::println);

//        System.out.println("try serializer");
//        System.out.println("-".repeat(40));
        //procs = Serializer.fromBytes( Serializer.toBytes(procs) );
        //procs.forEach(System.out::println);
    }
}
