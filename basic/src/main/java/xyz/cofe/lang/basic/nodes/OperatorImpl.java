package xyz.cofe.lang.basic.nodes;

import scala.Function1;
import scala.collection.Seq;

import java.util.function.BiConsumer;

public class OperatorImpl implements Function1<Seq<Object>,Object> {
    @Override
    public Object apply(Seq<Object> v1) {
        return null;
    }

    @Override
    public <A> Function1<A, Object> compose(Function1<A, Seq<Object>> g) {
        return x -> apply(g.apply(x));
    }

    @Override
    public <A> Function1<Seq<Object>, A> andThen(Function1<Object, A> g) {
        return x -> g.apply(apply(x));
    }

    public static class CompilerOpImpl <T extends AST<?,?>>
        extends OperatorImpl
        implements ASTCompiler
    {
        public final BiConsumer<T,Compiler> bytecodeWriter;
        public final Class<T> astType;
        public CompilerOpImpl( Class<T> astType, BiConsumer<T, Compiler> bytecodeWriter ) {
            if( bytecodeWriter==null )throw new IllegalArgumentException( "bytecodeWriter==null" );
            this.bytecodeWriter = bytecodeWriter;
            this.astType = astType;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean compile( AST<?, ?> ast, Compiler compiler ) {
            if( ast==null )throw new IllegalArgumentException( "ast==null" );
            if( compiler==null )throw new IllegalArgumentException( "compiler==null" );
            if( astType!=null ){
                if( astType.isAssignableFrom(ast.getClass()) ){
                    bytecodeWriter.accept((T) ast, compiler);
                    return true;
                }else {
                    return false;
                }
            }else{
                bytecodeWriter.accept((T)ast, compiler);
                return true;
            }
        }
    }

    public static <T extends AST<?,?>> CompilerOpImpl<T> compiler(Class<T> cls, BiConsumer<T,Compiler> writer){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        return new CompilerOpImpl<>(cls,writer);
    }

    public static <T extends AST<?,?>> CompilerOpImpl<T> compiler(BiConsumer<T,Compiler> writer){
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        return new CompilerOpImpl<>(null,writer);
    }
}
