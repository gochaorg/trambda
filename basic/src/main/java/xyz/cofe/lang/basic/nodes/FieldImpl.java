package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.Field;
import xyz.cofe.stsl.types.Type;

public class FieldImpl extends Field implements ASTCompiler {
    protected ASTCompiler compiler;
    public FieldImpl( String name, Type tip, ASTCompiler compiler ) {
        super(name, tip);
        this.compiler = compiler;
    }

    @Override
    public boolean compile( AST<?, ?> ast, Compiler compiler ) {
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( compiler==null )throw new IllegalArgumentException( "compiler==null" );
        return this.compiler.compile(ast,compiler);
    }
}
