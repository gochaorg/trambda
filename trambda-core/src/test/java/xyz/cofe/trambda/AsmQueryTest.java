package xyz.cofe.trambda;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import xyz.cofe.fn.Fn1;
import xyz.cofe.io.fs.File;

public class AsmQueryTest {
    @Test
    public void test01(){
        System.out.println("test01"+"=".repeat(50));
        new AsmQuery<String>().apply( env -> env.contains("yuui") );
    }

    @Test
    public void getByteCode(){
        System.out.println("getByteCode");

        AtomicReference<LambdaDump> dump1 = new AtomicReference<>();

        new AsmQuery<String>(){
            /**
             * Реализация вызова лямбды
             *
             * @param fn   лямбда
             * @param sl   лямбда - сериализация
             * @param dump байт-код лямбды
             * @return результат вызова
             */
            @Override
            protected <RES> RES call(Fn1<String, RES> fn, SerializedLambda sl, LambdaDump dump){
                dump1.set(dump);
                return super.call(fn, sl, dump);
            }
        }.apply(env -> env+env );

        if( dump1.get()!=null ){
            File file = new File("target/test/byteCode/AsmQueryTest/getByteCode.dat");
            File dir = file.getParent();
            if( !dir.exists() )dir.createDirectories();

            try( var strm = file.writeStream() ){
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(strm);
                objectOutputStream.writeObject(
                    dump1.get()
                );
                objectOutputStream.flush();
                System.out.println("writed "+file);
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static class SomeC01 {
        public final String name;
        public SomeC01(String name){
            this.name = name;
        }
    }

    @Test
    public void testClosure01(){
        System.out.println("test01"+"=".repeat(50));

        var what1 = "yuui";
        var what2 = "uuby";
        new AsmQuery<SomeC01>().apply(
            env -> env.name.contains(what1+what2)
        );
    }
}
