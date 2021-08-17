package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Базовые типы
 */
public class BaseTypes {
    public static final BaseTypes instance = new BaseTypes();

    public final TObject INT = TObject.create("int")
        .methods( meths -> {
            meths.method( mb -> {mb
                .name("+")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( new OperatorImpl("add(int,int)->int") );
                mb.add();
            });
            meths.method( mb -> {mb
                .name("-")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( new OperatorImpl("sub(int,int)->int") );
                mb.add();
            });
            meths.method( mb -> {mb
                .name("*")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( new OperatorImpl("mul(int,int)->int") );
                mb.add();
            });
            meths.method( mb -> {mb
                .name("/")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( new OperatorImpl("div(int,int)->int") );
                mb.add();
            });
        })
        .build();

    public final TObject STRING = TObject.create("string")
        .fileds( fields -> {
            fields.fileld("length", INT).add();
        })
        .methods( meths -> {
            meths.method( mb -> {mb
                .name("+")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( new OperatorImpl("add(string,string)->string") );
                mb.add();
            });
        })
        .build();

    public final Type[] types = new Type[] {
        INT, STRING
    };

    public final Map<Type,String> primitiveName;
    {
        /*
        https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2-200

        Java type  Type descriptor
        boolean     Z
        char        C
        byte        B
        short       S
        int         I
        float       F
        long        J
        double      D
        Object      Ljava/lang/Object;
        int[]       [I
        Object[][]  [[Ljava/lang/Object;
        void        V
         */

        Map<Type,String> names = new LinkedHashMap<>();
        names.put(INT,"I");
        names.put(STRING,"L"+String.class.getName().replace(".","/")+";");
        names.put(Type.VOID(),"V");
        primitiveName = Collections.unmodifiableMap(names);
    }
}
