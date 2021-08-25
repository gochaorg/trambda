package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;
import xyz.cofe.trambda.bc.mth.MInsn;
import xyz.cofe.trambda.bc.mth.MMethodInsn;
import xyz.cofe.trambda.bc.mth.MVarInsn;
import xyz.cofe.trambda.bc.mth.OpCode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static xyz.cofe.lang.basic.nodes.OperatorImpl.*;

/**
 * Базовые типы которые представлены в языке
 *
 * <br>
 * Включают в себя примитивы JVM и некоторые типы из JVM
 */
public class BaseTypes {
    /**
     * Экземпляр
     */
    public static final BaseTypes instance = new BaseTypes();

    /**
     * Цело число, со знаком, 32 бит
     */
    public final TObject INT = TObject.create("int")
        .methods( meths -> {
            meths.method( mb -> {mb
                .name("+")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( compiler((BinOpAST ast,Compiler compiler)->{
                    compiler.compile(ast.getLeft());
                    compiler.compile(ast.getRight());
                    compiler.method().getMethodByteCodes().add(new MInsn(OpCode.IADD.code));
                }));
                mb.add();
            });
            meths.method( mb -> {mb
                .name("-")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                //.setCall( new OperatorImpl() );
                .setCall( compiler((BinOpAST ast,Compiler compiler)->{
                    compiler.compile(ast.getLeft());
                    compiler.compile(ast.getRight());
                    compiler.method().getMethodByteCodes().add(new MInsn(OpCode.ISUB.code));
                }));
                mb.add();
            });
            meths.method( mb -> {mb
                .name("*")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                //.setCall( new OperatorImpl() );
                .setCall( compiler((BinOpAST ast,Compiler compiler)->{
                    compiler.compile(ast.getLeft());
                    compiler.compile(ast.getRight());
                    compiler.method().getMethodByteCodes().add(new MInsn(OpCode.IMUL.code));
                }));
                mb.add();
            });
            meths.method( mb -> {mb
                .name("/")
                .params( p -> p.param("this", Type.THIS()).param("num", Type.THIS()) )
                .result( Type.THIS() )
                //.setCall( new OperatorImpl() );
                .setCall( compiler((BinOpAST ast,Compiler compiler)->{
                    compiler.compile(ast.getLeft());
                    compiler.compile(ast.getRight());
                    compiler.method().getMethodByteCodes().add(new MInsn(OpCode.IDIV.code));
                }));
                mb.add();
            });
        })
        .build();

    /**
     * Строка unicode, 16 бит на символ.
     */
    public final TObject STRING = TObject.create("string")
        .fileds( fields -> {
            //fields.fileld("length", INT).add();
            fields.field(
                new FieldImpl("length", INT, (( ast, compiler ) -> {
//                    Integer idx = compiler.varIndex(ast.getV) //varIndex.get(ast.getVarName());
//                    if( idx==null )throw new Error("for variable "+ast.getVarName()+" index not found");
//
//                    compiler.method().getMethodByteCodes().add(
//                        new MVarInsn(OpCode.ALOAD.code, )
//                    );
                    if( ast instanceof ObjAccessAST ){
                        var objacc = (ObjAccessAST)ast;
                        var varname = objacc.getVarName();

                        Integer idx = compiler.varIndex(varname); //varIndex.get(ast.getVarName());
                        if( idx==null )throw new Error("for variable "+varname+" index not found");

                        compiler.method().getMethodByteCodes().add(
                            new MVarInsn(OpCode.ALOAD.code, idx)
                        );
                    }else{
                        throw new Error("ast not instance of ObjAccessAST");
                    }

                    compiler.method().getMethodByteCodes().add(
                        new MMethodInsn(
                            OpCode.INVOKEVIRTUAL.code,
                            "java/lang/String",
                            "length",
                            "()I",
                            false
                        )
                    );
                    return true;
                })
            )).add();
        })
        .methods( meths -> {
            meths.method( mb -> {mb
                .name("+")
                .params( p -> p.param("this", Type.THIS()).param("str", Type.THIS()) )
                .result( Type.THIS() )
                .setCall( new OperatorImpl() );
                mb.add();
            });
        })
        .build();

    /**
     * Проверяет что тип относится к примитивному JVM типу
     * @param type тип
     * @return true - является примитивным
     */
    public boolean isJvmPrimitive( Type type ){
        if( type==null )throw new IllegalArgumentException( "type==null" );
        return primitiveName.get(type) != null;
    }

    //region primitiveName : Map<Type,String> -  примитивное название типа (jvm примитив)
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
        names.put(Type.VOID(),"V");
        primitiveName = Collections.unmodifiableMap(names);
    }
    //endregion

    //region types : Type[] - Перечень базовых типов
    /**
     * Перечень базовых типов
     */
    public final Type[] types = new Type[] {
        INT, STRING
    };
    //endregion
}
