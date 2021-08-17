// Generated from /home/uzer/code/trambda/basic/src/main/java/xyz/cofe/lang/basic/Basic.g4 by ANTLR 4.9.1
package xyz.cofe.lang.basic;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BasicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BasicVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BasicParser#r}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitR(BasicParser.RContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(BasicParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(BasicParser.ArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg(BasicParser.ArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#fnReturn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnReturn(BasicParser.FnReturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(BasicParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(BasicParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryOp}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOp(BasicParser.UnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomValue}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomValue(BasicParser.AtomValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinOp}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinOp(BasicParser.BinOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentheses(BasicParser.ParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(BasicParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#varRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarRef(BasicParser.VarRefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BasicParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(BasicParser.LiteralContext ctx);
}