package xyz.cofe.trambda;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import xyz.cofe.io.fs.File;
import xyz.cofe.trambda.bc.MethodDef;

public class BuildMethodTest {
    @Test
    public void build1(){
        System.out.println("build1");
        System.out.println("=================");

        File file = new File("target/test/byteCode/AsmQueryTest/getByteCode.dat");
        if( !file.exists() ){
            System.out.println("not found "+file);
            return;
        }

        MethodDef mdef = null;
        try( var strm = file.readStream() ){
            var objectStream = new ObjectInputStream(strm);
            mdef = (MethodDef) objectStream.readObject();
        } catch( IOException | ClassNotFoundException e ) {
            e.printStackTrace();
            return;
        }

        System.out.println("read method:");
        System.out.println("name "+mdef.getName());
        System.out.println("desc "+mdef.getDescriptor());
        System.out.println("sign "+mdef.getSignature());
        System.out.println("accs "+mdef.getAccess());

        var mrest = new MethodRestore().className("xyz.cofe.trambda.buildMethodTest.Build1");
        var byteCode = mrest.methodName("lambda1").methodDef(mdef).generate();

        File target = new File(
            "target/test-classes-gen/"+
                mrest.getClassName().replace(".","/")+".class");

        File dir = target.getParent();
        if( dir!=null && !dir.exists() )dir.createDirectories();

        target.write(byteCode);

        System.out.println("writed "+target);
    }

    @Test
    public void tryExec(){
        System.out.println("tryExec");
        System.out.println("=".repeat(60));

        File file = new File("target/test/byteCode/AsmQueryTest/getByteCode.dat");
        if( !file.exists() ){
            System.out.println("not found "+file);
            return;
        }

        MethodDef mdef = null;
        try( var strm = file.readStream() ){
            var objectStream = new ObjectInputStream(strm);
            mdef = (MethodDef) objectStream.readObject();
        } catch( IOException | ClassNotFoundException e ) {
            e.printStackTrace();
            return;
        }

        var clName = "xyz.cofe.trambda.buildMethodTest.Build1";
        var methName = "lambda1";

        var byteCode = new MethodRestore()
            .className(clName)
            .methodName("lambda1")
            .methodDef(mdef)
            .generate();

        ClassLoader cl = new ClassLoader(ClassLoader.getSystemClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException{
                if( name!=null && name.equals(clName) ){
                    return defineClass(name,byteCode,0,byteCode.length);
                }
                return super.findClass(name);
            }
        };

        System.out.println("try read class "+clName);
        Class c = null;
        try{
            c = Class.forName(clName,true,cl);
            System.out.println("class found "+c);
        } catch( ClassNotFoundException e ) {
            e.printStackTrace();
            return;
        }

        Method m = null;
        System.out.println("methods");
        for( var delMeth : c.getDeclaredMethods() ){
            System.out.println(""+delMeth);
            if( delMeth.getName().equals(methName) ){
                m = delMeth;
            }
        }

        if( m==null ){
            System.out.println("not found "+methName);
            return;
        }

        try{
            Object arg0 = "abc";
            System.out.println("call with "+arg0);
            Object res = m.invoke(null, arg0);
            System.out.println("result "+res);
        } catch( IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
}
