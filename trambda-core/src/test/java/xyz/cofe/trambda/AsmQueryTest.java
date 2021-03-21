package xyz.cofe.trambda;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import xyz.cofe.io.fs.File;
import xyz.cofe.trambda.bc.MethodDef;

public class AsmQueryTest {
    @Test
    public void test01(){
        System.out.println("test01"+"=".repeat(50));
        new AsmQuery<String>().apply( env -> env.contains("yuui") );
    }

    @Test
    public void getByteCode(){
        System.out.println("getByteCode");

        var refMthods = new AtomicReference<Map<String, MethodDef>>();
        new AsmQuery<String>(){
            {
                refMthods.set(methods);
            }
        }.apply( env -> env+env );

        if( refMthods.get()!=null ){
            System.out.println("methods found, count="+refMthods.get().size());
            File file = new File("target/test/byteCode/AsmQueryTest/getByteCode.dat");
            File dir = file.getParent();
            if( !dir.exists() )dir.createDirectories();

            try( var strm = file.writeStream() ){
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(strm);
                objectOutputStream.writeObject(
                    refMthods.get().values().iterator().next()
                );
                objectOutputStream.flush();
                System.out.println("writed "+file);
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    //@Test
    public void readByteCodeFromFile(){
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

        mdef.getByteCodes().stream().map(b->"  "+b.toString()).forEach(System.out::println);
    }
}
