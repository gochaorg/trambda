package xyz.cofe.lang.basic.nodes;

import org.antlr.v4.runtime.ParserRuleContext;
import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.TreeStep;
import xyz.cofe.lang.basic.BasicParser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract Syntax Tree (AST) - узел вбстрактного синтаксического дерева
 * @param <R> ссылка на antlr узел/правило
 * @param <C> тип дочерних узлов
 */
public abstract class AST<R extends ParserRuleContext,C extends AST<? extends ParserRuleContext, ?>> {
    public AST(R antlrRule){
        if( antlrRule==null )throw new IllegalArgumentException( "antlrRule==null" );
        this.antlrRule = antlrRule;
    }

    //region antlrRule : R - ссылка на antlr узел/правило
    /**
     * ссылка на antlr узел/правило
     */
    protected final R antlrRule;

    /**
     * Возвращает ссылку на antlr узел/правило
     * @return ссылка на antlr узел/правило
     */
    public R getAntlrRule() {
        return antlrRule;
    }
    //endregion

    //region tree structure
    //region parent
    protected AST<?,?> parent;
    public AST<?, ?> getParent() { return parent; }
    protected void setParent(AST<?, ?> parent) { this.parent = parent; }
    //endregion
    //region children
    protected List<C> children;
    public List<C> getChildren(){
        if( children!=null)return children;
        children = new ArrayList<>();
        var c1 = createChildren();
        if( c1!=null ){
            for( var c : c1 ){
                if( c!=null ){
                    children.add(c);
                    c.setParent(this);
                }
            }
        }
        return children;
    }
    //endregion
    //region createChildren()
    protected abstract List<C> createChildren();
    //endregion

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Eterable<TreeStep<AST<?,?>>> tree(){
        AST<?,?> x = this;
        var e = Eterable.tree(x, n -> (List) n.getChildren()).go();
        return (Eterable)e;
    }
    //endregion

    //region wrap(..)
    public static FunAST wrap(FunctionContext f){
        if( f==null )throw new IllegalArgumentException( "f==null" );
        return new FunAST(f);
    }
    public static ArgAST wrap(ArgContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new ArgAST(c);
    }
    public static ArgsAST wrap(ArgsContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new ArgsAST(c);
    }
    public static AtomAST wrap(AtomContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new AtomAST(c);
    }
    public static AtomValueAST wrap(AtomValueContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new AtomValueAST(c);
    }
    public static BinOpAST wrap(BinOpContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new BinOpAST(c);
    }
    public static FnReturnAST wrap(FnReturnContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new FnReturnAST(c);
    }
    public static LiteralAST wrap(LiteralContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new LiteralAST(c);
    }
    public static ParenthesesAST wrap(ParenthesesContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new ParenthesesAST(c);
    }
    public static ReturnAST wrap(ReturnStatementContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new ReturnAST(c);
    }
    public static StatementAST wrap(StatementContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new StatementAST(c);
    }
    public static UnaryOpAST wrap(UnaryOpContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new UnaryOpAST(c);
    }
    public static VarRefAST wrap(VarRefContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        return new VarRefAST(c);
    }
    public static AST<?,?> wrap(ParserRuleContext c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        if( c instanceof FunctionContext )return wrap((FunctionContext) c);
        if( c instanceof ArgContext )return wrap((ArgContext) c);
        if( c instanceof ArgsContext )return wrap((ArgsContext) c);
        if( c instanceof AtomContext )return wrap((AtomContext) c);
        if( c instanceof AtomValueContext )return wrap((AtomValueContext) c);
        if( c instanceof BinOpContext )return wrap((BinOpContext) c);
        if( c instanceof FnReturnContext )return wrap((FnReturnContext) c);
        if( c instanceof LiteralContext )return wrap((LiteralContext) c);
        if( c instanceof ParenthesesContext )return wrap((ParenthesesContext) c);
        if( c instanceof ReturnStatementContext )return wrap((ReturnStatementContext) c);
        if( c instanceof StatementContext )return wrap((StatementContext) c);
        if( c instanceof UnaryOpContext )return wrap((UnaryOpContext) c);
        if( c instanceof VarRefContext )return wrap((VarRefContext) c);
        throw new IllegalArgumentException("unsupported "+c.getClass().getName());
    }
    //endregion

    @SuppressWarnings("unchecked")
    public <T extends AST<?,?>> Optional<T> find( Class<T> cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        AST<?,?> a = this;
        //noinspection ConditionalBreakInInfiniteLoop
        while (true){
            if( cls.isAssignableFrom(a.getClass()) )return Optional.of( (T)a );
            a = a.getParent();
            if( a==null )break;
        }
        return Optional.empty();
    }
}
