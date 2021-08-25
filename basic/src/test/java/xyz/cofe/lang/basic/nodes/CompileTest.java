package xyz.cofe.lang.basic.nodes;

import org.junit.jupiter.api.Test;
import xyz.cofe.bc.xml.BCSeriliazer;
import xyz.cofe.trambda.bc.cls.CBegin;

import java.lang.reflect.InvocationTargetException;

import static xyz.cofe.lang.basic.nodes.ToasterTest.*;
import xyz.cofe.trambda.bc.cls.CMethod;

public class CompileTest {
    private Object tryFunCall( String src, String className, String funName, Object ... args ){
        var ast = funAST(src);
        var toaster = new Toaster();
        toaster.resolve(ast);
        dump(ast);

        var compiler = new Compiler();
        CMethod cm = null;
        try {
            cm = compiler.compile(ast);
        } catch( Throwable err ){
            System.out.println(err);
            throw err;
        }

        new BCSeriliazer().write(cm);

        var clazz = new CBegin();

        clazz.javaName().setName(className);
        clazz.setSuperName(Object.class.getName().replace(".","/"));
        clazz.setPublic(true);
        clazz.setSuper(true);
        clazz.getMethods().add( cm );
        clazz.setVersion(55);

        ClassLoader cl = new ClassLoader() {
            @Override
            protected Class<?> findClass( String name ) throws ClassNotFoundException {
                if( name.equals(className)){
                    var bytes = clazz.toByteCode();
                    return defineClass(className,bytes,0,bytes.length);
                }
                return super.findClass(name);
            }
        };

        try {
            Class c1 = Class.forName(className,true,cl);
            System.out.println("class '"+className+"' loaded");

            for( var m : c1.getMethods() ){
                System.out.println("found method "+m);
                if( m.getName().equals(funName) ){
                    try {
                        System.out.println("try invoke");
                        var res = m.invoke(null,args);
                        System.out.println("result = "+res);
                        return res;
                    } catch ( IllegalAccessException | InvocationTargetException e ) {
                        throw new Error(e);
                    }
                }
            }
            throw new Error("method "+funName+" not found in  "+c1);
        } catch ( ClassNotFoundException e ) {
            throw new Error(e);
        }
    }
    
    @Test
    public void test01(){
        var res = tryFunCall(
            "fn add( a:int, b:int ):int { return a+b }", 
            "xyz.cofe.lang.basic.nodes.compile_test.t1", 
            "add", 12,13);
    }
    
    @Test
    public void test02(){
        var res = tryFunCall(
            "fn len1( a:string ):int { return a.length }", 
            "xyz.cofe.lang.basic.nodes.compile_test.t2", 
            "len1", "abcde");
    }
}
