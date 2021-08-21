package xyz.cofe.lang.basic.nodes;

public interface ASTCompiler {
    boolean compile( AST<?,?> ast, Compiler compiler );
}
