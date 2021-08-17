// Generated from /home/uzer/code/trambda/basic/src/main/java/xyz/cofe/lang/basic/Basic.g4 by ANTLR 4.9.1
package xyz.cofe.lang.basic;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BasicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, RETURN=13, ID=14, NUMBER=15, STRING=16, WS=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "NameChar", "NameStartChar", "RETURN", "ID", 
			"HEX_DIGIT", "DIGIT", "NUMBER", "ESC", "STRING", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'fn'", "'{'", "'}'", "'('", "','", "')'", "':'", "'*'", "'/'", 
			"'+'", "'-'", "'!'", "'return'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "RETURN", "ID", "NUMBER", "STRING", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public BasicLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Basic.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23\u009f\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3"+
		"\13\3\f\3\f\3\r\3\r\3\16\3\16\5\16K\n\16\3\17\3\17\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\7\21X\n\21\f\21\16\21[\13\21\3\22\3\22\3\23"+
		"\3\23\3\24\6\24b\n\24\r\24\16\24c\3\24\3\24\7\24h\n\24\f\24\16\24k\13"+
		"\24\5\24m\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0083\n\25\3\26\3\26"+
		"\3\26\7\26\u0088\n\26\f\26\16\26\u008b\13\26\3\26\3\26\3\26\3\26\7\26"+
		"\u0091\n\26\f\26\16\26\u0094\13\26\3\26\5\26\u0097\n\26\3\27\6\27\u009a"+
		"\n\27\r\27\16\27\u009b\3\27\3\27\2\2\30\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\2\35\2\37\17!\20#\2%\2\'\21)\2+\22-\23"+
		"\3\2\b\7\2\62;aa\u00b9\u00b9\u0302\u0371\u2041\u2042\17\2C\\c|\u00c2\u00d8"+
		"\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191"+
		"\u2c02\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff\5\2\62;CHch\3\2\62;\6"+
		"\2\f\f\17\17$$^^\5\2\13\f\17\17\"\"\2\u00aa\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\37\3\2\2\2"+
		"\2!\3\2\2\2\2\'\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\3/\3\2\2\2\5\62\3\2\2\2"+
		"\7\64\3\2\2\2\t\66\3\2\2\2\138\3\2\2\2\r:\3\2\2\2\17<\3\2\2\2\21>\3\2"+
		"\2\2\23@\3\2\2\2\25B\3\2\2\2\27D\3\2\2\2\31F\3\2\2\2\33J\3\2\2\2\35L\3"+
		"\2\2\2\37N\3\2\2\2!U\3\2\2\2#\\\3\2\2\2%^\3\2\2\2\'a\3\2\2\2)\u0082\3"+
		"\2\2\2+\u0096\3\2\2\2-\u0099\3\2\2\2/\60\7h\2\2\60\61\7p\2\2\61\4\3\2"+
		"\2\2\62\63\7}\2\2\63\6\3\2\2\2\64\65\7\177\2\2\65\b\3\2\2\2\66\67\7*\2"+
		"\2\67\n\3\2\2\289\7.\2\29\f\3\2\2\2:;\7+\2\2;\16\3\2\2\2<=\7<\2\2=\20"+
		"\3\2\2\2>?\7,\2\2?\22\3\2\2\2@A\7\61\2\2A\24\3\2\2\2BC\7-\2\2C\26\3\2"+
		"\2\2DE\7/\2\2E\30\3\2\2\2FG\7#\2\2G\32\3\2\2\2HK\5\35\17\2IK\t\2\2\2J"+
		"H\3\2\2\2JI\3\2\2\2K\34\3\2\2\2LM\t\3\2\2M\36\3\2\2\2NO\7t\2\2OP\7g\2"+
		"\2PQ\7v\2\2QR\7w\2\2RS\7t\2\2ST\7p\2\2T \3\2\2\2UY\5\35\17\2VX\5\33\16"+
		"\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\"\3\2\2\2[Y\3\2\2\2\\]\t\4"+
		"\2\2]$\3\2\2\2^_\t\5\2\2_&\3\2\2\2`b\5%\23\2a`\3\2\2\2bc\3\2\2\2ca\3\2"+
		"\2\2cd\3\2\2\2dl\3\2\2\2ei\7\60\2\2fh\5%\23\2gf\3\2\2\2hk\3\2\2\2ig\3"+
		"\2\2\2ij\3\2\2\2jm\3\2\2\2ki\3\2\2\2le\3\2\2\2lm\3\2\2\2m(\3\2\2\2no\7"+
		"^\2\2o\u0083\7$\2\2pq\7^\2\2q\u0083\7^\2\2rs\7^\2\2s\u0083\7)\2\2tu\7"+
		"^\2\2u\u0083\7t\2\2vw\7^\2\2w\u0083\7p\2\2xy\7^\2\2y\u0083\7v\2\2z{\7"+
		"^\2\2{|\7z\2\2|}\3\2\2\2}~\5#\22\2~\177\5#\22\2\177\u0080\5#\22\2\u0080"+
		"\u0081\5#\22\2\u0081\u0083\3\2\2\2\u0082n\3\2\2\2\u0082p\3\2\2\2\u0082"+
		"r\3\2\2\2\u0082t\3\2\2\2\u0082v\3\2\2\2\u0082x\3\2\2\2\u0082z\3\2\2\2"+
		"\u0083*\3\2\2\2\u0084\u0089\7$\2\2\u0085\u0088\5)\25\2\u0086\u0088\n\6"+
		"\2\2\u0087\u0085\3\2\2\2\u0087\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089"+
		"\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008c\3\2\2\2\u008b\u0089\3\2"+
		"\2\2\u008c\u0097\7$\2\2\u008d\u0092\7)\2\2\u008e\u0091\5)\25\2\u008f\u0091"+
		"\n\6\2\2\u0090\u008e\3\2\2\2\u0090\u008f\3\2\2\2\u0091\u0094\3\2\2\2\u0092"+
		"\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0095\3\2\2\2\u0094\u0092\3\2"+
		"\2\2\u0095\u0097\7)\2\2\u0096\u0084\3\2\2\2\u0096\u008d\3\2\2\2\u0097"+
		",\3\2\2\2\u0098\u009a\t\7\2\2\u0099\u0098\3\2\2\2\u009a\u009b\3\2\2\2"+
		"\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e"+
		"\b\27\2\2\u009e.\3\2\2\2\17\2JYcil\u0082\u0087\u0089\u0090\u0092\u0096"+
		"\u009b\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}