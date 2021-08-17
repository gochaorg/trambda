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
	 * Enter a parse tree produced by {@link BasicParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(BasicParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(BasicParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#args}.
	 * @param ctx the parse tree
	 */
	void enterArgs(BasicParser.ArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#args}.
	 * @param ctx the parse tree
	 */
	void exitArgs(BasicParser.ArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterArg(BasicParser.ArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitArg(BasicParser.ArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#fnReturn}.
	 * @param ctx the parse tree
	 */
	void enterFnReturn(BasicParser.FnReturnContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#fnReturn}.
	 * @param ctx the parse tree
	 */
	void exitFnReturn(BasicParser.FnReturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(BasicParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(BasicParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(BasicParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(BasicParser.ReturnStatementContext ctx);
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
	 * Enter a parse tree produced by the {@code AtomValue}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAtomValue(BasicParser.AtomValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtomValue}
	 * labeled alternative in {@link BasicParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAtomValue(BasicParser.AtomValueContext ctx);
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
	 * Enter a parse tree produced by {@link BasicParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(BasicParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(BasicParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link BasicParser#varRef}.
	 * @param ctx the parse tree
	 */
	void enterVarRef(BasicParser.VarRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BasicParser#varRef}.
	 * @param ctx the parse tree
	 */
	void exitVarRef(BasicParser.VarRefContext ctx);
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