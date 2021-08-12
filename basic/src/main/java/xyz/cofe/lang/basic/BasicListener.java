// Generated from /home/uzer/code/trambda/basic/src/main/java/xyz/cofe/lang/basic/Basic.g4 by ANTLR 4.9.1
package xyz.cofe.lang.basic;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BasicParser}.
 */
public interface BasicListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BasicParser#r}.
	 * @param ctx the parse tree
	 */
	void enterR(BasicParser.RContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#r}.
	 * @param ctx the parse tree
	 */
	void exitR(BasicParser.RContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryOp}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOp(BasicParser.UnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryOp}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOp(BasicParser.UnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralValue}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLiteralValue(BasicParser.LiteralValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralValue}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLiteralValue(BasicParser.LiteralValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinOp}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinOp(BasicParser.BinOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinOp}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinOp(BasicParser.BinOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParentheses(BasicParser.ParenthesesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParentheses(BasicParser.ParenthesesContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(BasicParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(BasicParser.LiteralContext ctx);
}