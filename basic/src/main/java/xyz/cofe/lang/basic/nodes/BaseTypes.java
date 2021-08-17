package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

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
}
