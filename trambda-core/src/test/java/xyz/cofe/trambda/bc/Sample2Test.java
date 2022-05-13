package xyz.cofe.trambda.bc;

import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.bc.bm.LdcType;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.mth.MLdcInsn;
import xyz.cofe.trambda.bc.mth.MMethodInsn;
import xyz.cofe.trambda.bc.mth.MethodByteCode;
import xyz.cofe.trambda.bc.mth.OpCode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Sample2Test {
    // будем модифицировать ее
    public static int some(int a, int b) {
        if( a>b )throw new IllegalArgumentException("a > b");
        int cnt = 0;
        for( int i=a; i<=b; i++ ){
            cnt += i;
        }
        return cnt;
    }
    // этот код будеть внедряться
    public static void sampleEcho(){
        checkPoint("sampleEcho");
    }
    // отметка о прохождении контрольной точки
    public static void checkPoint( String pointName ){
        System.out.println("CP "+pointName);
    }

    @Test
    public void modifyTest(){
        var originalCBegin = CBegin.parseByteCode(Sample2Test.class);

        var someMethodOpt = originalCBegin.getMethods().stream().filter(m -> m.getName().equals("some")).findFirst();
        if( someMethodOpt.isPresent() ){
            System.out.println("found "+someMethodOpt.get().getName());
        }else{
            System.out.println("not found \"some\" method");
            return;
        }

        dump(someMethodOpt.get());

        var injections = new TreeMap<Integer,List<MethodByteCode>>();
        injections.put(1, checkpointByteCode("intro"));
        injections.put(39, checkpointByteCode("return"));
        injections.put(14, checkpointByteCode("frame 1"));
        injections.put(22, checkpointByteCode("frame 2"));

        injections.descendingMap().forEach( (pos,inject) ->
            someMethodOpt.get().getMethodByteCodes().addAll(pos,inject)
        );

        //originalCBegin.javaName().setName("generated.Gen1");
        var cloader = classLoader(
            originalCBegin,
            this.getClass().getClassLoader());
        try {
            System.out.println("- ".repeat(30));
            System.out.println("try load class");
            var generatedClass = Class.forName(originalCBegin.javaName().getName(),true,cloader);

            System.out.println("loaded "+generatedClass);
            var meth = generatedClass.getDeclaredMethod("some", int.class, int.class);

            System.out.println("call "+meth+"\n  args: 10,20");
            var res = meth.invoke(null, 10, 20);
            System.out.println("result "+res);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private List<MethodByteCode> checkpointByteCode(String pointName){
        var introByteCode = new ArrayList<MethodByteCode>();
        introByteCode.add(new MLdcInsn(pointName, LdcType.String));
        introByteCode.add(new MMethodInsn(
            OpCode.INVOKESTATIC.code,
            Sample2Test.class.getName().replace(".","/"),
            "checkPoint",
            "(Ljava/lang/String;)V", false));
        return introByteCode;
    }

    private void dump( CMethod method){
        System.out.println(method);
        for( var mi=0;mi<method.getMethodByteCodes().size();mi++ ){
            System.out.print("["+mi+"] ");
            System.out.println(method.getMethodByteCodes().get(mi));
        }
    }

    private ClassLoader classLoader(CBegin cBegin, ClassLoader parentCls){
        return new ClassLoader(parentCls) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if(name.equals(cBegin.javaName().getName())){
                    var bytes = cBegin.toByteCode();
                    //System.out.println("loadClass("+name+") matched");
                    return defineClass(name,bytes,0,bytes.length);
                }
                var res = super.loadClass(name);
                //System.out.println("loadClass("+name+") super "+res);
                return res;
            }
        };
    }
}
