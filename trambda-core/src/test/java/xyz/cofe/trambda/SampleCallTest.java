package xyz.cofe.trambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.cofe.io.fs.File;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.bc.MethodDef;
import xyz.cofe.trambda.sample.EnvLocal;
import xyz.cofe.trambda.sample.IEnv;
import xyz.cofe.trambda.sample.User;

public class SampleCallTest {
    @Test
    public void test01(){
        System.out.println("test01");
        System.out.println("===========================");

        System.out.println("get mdef");

        AtomicReference<MethodDef> mdefRef = new AtomicReference<>();
        IEnv env = new EnvLocal();
        var res =
            new AsmQuery<IEnv>(){
                @Override
                protected <RES> RES call(Fn<IEnv, RES> fn, SerializedLambda sl, MethodDef mdef){
                    mdefRef.set(mdef);
                    return super.call(fn, sl, mdef);
                }
            }.apply(
                env0 -> env0.getUsers().filter(u -> u.getName().contains("Petrov"))
            );

        var mdef = mdefRef.get();
        Assertions.assertTrue(mdef!=null);

        System.out.println("mdef fetched");

        String genClsName = SampleCallTest.class.getName().toLowerCase()+".Test01";
        String genMethName = "test01call";
        byte[] byteCode = new MethodRestore()
            .className(genClsName)
            .methodName(genMethName)
            .methodDef(mdef)
            .generate();

        System.out.println("byte code len = "+byteCode.length);

        File byteCodeFile =
            new File("target/test/"+SampleCallTest.class.getSimpleName())
            .resolve(
                new File(genClsName.replace('.','/')+".class")
            );

        File dir = byteCodeFile.getParent();
        if( dir!=null && !dir.exists() ){
            dir.createDirectories();
        }

        byteCodeFile.write(byteCode);
        System.out.println("writed "+byteCodeFile);

        ClassLoader cl = new ClassLoader(this.getClass().getClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException{
                if( name!=null && name.equals(genClsName) ){
                    return defineClass(name,byteCode,0,byteCode.length);
                }
                return super.findClass(name);
            }
        };

        Class c = null;
        try{
            c = Class.forName(genClsName,true,cl);
            System.out.println("class found "+c);
        } catch( ClassNotFoundException e ) {
            e.printStackTrace();
            return;
        }

        Method m = null;
        System.out.println("methods");
        for( var delMeth : c.getDeclaredMethods() ){
            System.out.println(""+delMeth);
            if( delMeth.getName().equals(genMethName) ){
                m = delMeth;
            }
        }

        if( m==null ){
            System.out.println("not found "+genMethName);
            return;
        }

        try{
            System.out.println("call with "+env);
            Object callRes = m.invoke(null, env);
            System.out.println("result "+callRes);
            if( callRes instanceof Iterable ){
                for( Object itm : ((Iterable<?>)callRes) ){
                    if( itm instanceof User ){
                        var u = (User)itm;
                        System.out.println("  name: "+ Text.align(u.getName(), Align.Begin, " ",20)+" email: "+u.getEmail());
                    }else{
                        System.out.println("item: "+itm);
                    }
                }
            }
        } catch( IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

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
}
