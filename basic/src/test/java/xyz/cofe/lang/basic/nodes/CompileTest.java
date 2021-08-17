package xyz.cofe.lang.basic.nodes;

import org.junit.jupiter.api.Test;
import xyz.cofe.bc.xml.BCSeriliazer;
import xyz.cofe.trambda.bc.cls.CBegin;

import java.lang.reflect.InvocationTargetException;

import static xyz.cofe.lang.basic.nodes.ToasterTest.*;

public class CompileTest {
    @Test
    public void test01(){
        var src = "fn add( a:int, b:int ):int { return a+b }";
        var ast = funAST(src);
        var toaster = new Toaster();
        toaster.resolve(ast);
        dump(ast);

        // compile to byte code

        var compiler = new Compiler();
        var cm = compiler.compile(ast);

        new BCSeriliazer().write(cm);

        var clazz = new CBegin();

        String clazzName = "xyz.cofe.lang.basic.nodes.compile_test.t1";
        clazz.javaName().setName(clazzName);
        clazz.setSuperName(Object.class.getName().replace(".","/"));
        clazz.setPublic(true);
        clazz.setSuper(true);
        clazz.getMethods().add( cm );
        clazz.setVersion(55);

        // нет надобности - в будущем удалить
        //clazz.setOpen(true);
        //clazz.setTransitive(true);

        ClassLoader cl = new ClassLoader() {
            @Override
            protected Class<?> findClass( String name ) throws ClassNotFoundException {
                if( name.equals(clazzName)){
                    var bytes = clazz.toByteCode();
                    return defineClass(clazzName,bytes,0,bytes.length);
                }
                return super.findClass(name);
            }
        };

        try {
            Class c1 = Class.forName(clazzName,true,cl);
            System.out.println("class loaded");

            for( var m : c1.getMethods() ){
                System.out.println("found method "+m);
                if( m.getName().equals("add") ){
                    try {
                        System.out.println("try invoke");
                        var res = m.invoke(null,10,12);
                        System.out.println("result = "+res);
                    } catch ( IllegalAccessException | InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }
}
