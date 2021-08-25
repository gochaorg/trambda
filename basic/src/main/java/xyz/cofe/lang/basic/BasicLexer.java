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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, RETURN=23, ID=24, NUMBER=25, 
		STRING=26, WS=27;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "NameChar", "NameStartChar", 
			"RETURN", "ID", "HEX_DIGIT", "DIGIT", "NUMBER", "ESC", "STRING", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'fn'", "'{'", "';'", "'}'", "'('", "','", "')'", "':'", "'|'", 
			"'&'", "'<'", "'>'", "'=='", "'!='", "'<='", "'>='", "'*'", "'/'", "'+'", 
			"'-'", "'!'", "'.'", "'return'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, "RETURN", 
			"ID", "NUMBER", "STRING", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\35\u00ca\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3"+
		"\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\5\30w\n\30\3\31\3\31\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\33\3\33\7\33\u0084\n\33\f\33\16\33\u0087\13\33\3\34"+
		"\3\34\3\35\3\35\3\36\6\36\u008e\n\36\r\36\16\36\u008f\3\36\3\36\6\36\u0094"+
		"\n\36\r\36\16\36\u0095\5\36\u0098\n\36\3\37\3\37\3\37\3\37\3\37\3\37\3"+
		"\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5"+
		"\37\u00ae\n\37\3 \3 \3 \7 \u00b3\n \f \16 \u00b6\13 \3 \3 \3 \3 \7 \u00bc"+
		"\n \f \16 \u00bf\13 \3 \5 \u00c2\n \3!\6!\u00c5\n!\r!\16!\u00c6\3!\3!"+
		"\2\2\"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17"+
		"\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\2\61\2\63\31\65\32\67\29\2"+
		";\33=\2?\34A\35\3\2\b\7\2\62;aa\u00b9\u00b9\u0302\u0371\u2041\u2042\17"+
		"\2C\\c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e"+
		"\u200f\u2072\u2191\u2c02\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff\5\2"+
		"\62;CHch\3\2\62;\6\2\f\f\17\17$$^^\5\2\13\f\17\17\"\"\2\u00d5\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2;\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\3C\3\2\2\2\5F\3\2\2\2\7H\3\2"+
		"\2\2\tJ\3\2\2\2\13L\3\2\2\2\rN\3\2\2\2\17P\3\2\2\2\21R\3\2\2\2\23T\3\2"+
		"\2\2\25V\3\2\2\2\27X\3\2\2\2\31Z\3\2\2\2\33\\\3\2\2\2\35_\3\2\2\2\37b"+
		"\3\2\2\2!e\3\2\2\2#h\3\2\2\2%j\3\2\2\2\'l\3\2\2\2)n\3\2\2\2+p\3\2\2\2"+
		"-r\3\2\2\2/v\3\2\2\2\61x\3\2\2\2\63z\3\2\2\2\65\u0081\3\2\2\2\67\u0088"+
		"\3\2\2\29\u008a\3\2\2\2;\u008d\3\2\2\2=\u00ad\3\2\2\2?\u00c1\3\2\2\2A"+
		"\u00c4\3\2\2\2CD\7h\2\2DE\7p\2\2E\4\3\2\2\2FG\7}\2\2G\6\3\2\2\2HI\7=\2"+
		"\2I\b\3\2\2\2JK\7\177\2\2K\n\3\2\2\2LM\7*\2\2M\f\3\2\2\2NO\7.\2\2O\16"+
		"\3\2\2\2PQ\7+\2\2Q\20\3\2\2\2RS\7<\2\2S\22\3\2\2\2TU\7~\2\2U\24\3\2\2"+
		"\2VW\7(\2\2W\26\3\2\2\2XY\7>\2\2Y\30\3\2\2\2Z[\7@\2\2[\32\3\2\2\2\\]\7"+
		"?\2\2]^\7?\2\2^\34\3\2\2\2_`\7#\2\2`a\7?\2\2a\36\3\2\2\2bc\7>\2\2cd\7"+
		"?\2\2d \3\2\2\2ef\7@\2\2fg\7?\2\2g\"\3\2\2\2hi\7,\2\2i$\3\2\2\2jk\7\61"+
		"\2\2k&\3\2\2\2lm\7-\2\2m(\3\2\2\2no\7/\2\2o*\3\2\2\2pq\7#\2\2q,\3\2\2"+
		"\2rs\7\60\2\2s.\3\2\2\2tw\5\61\31\2uw\t\2\2\2vt\3\2\2\2vu\3\2\2\2w\60"+
		"\3\2\2\2xy\t\3\2\2y\62\3\2\2\2z{\7t\2\2{|\7g\2\2|}\7v\2\2}~\7w\2\2~\177"+
		"\7t\2\2\177\u0080\7p\2\2\u0080\64\3\2\2\2\u0081\u0085\5\61\31\2\u0082"+
		"\u0084\5/\30\2\u0083\u0082\3\2\2\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2"+
		"\2\2\u0085\u0086\3\2\2\2\u0086\66\3\2\2\2\u0087\u0085\3\2\2\2\u0088\u0089"+
		"\t\4\2\2\u00898\3\2\2\2\u008a\u008b\t\5\2\2\u008b:\3\2\2\2\u008c\u008e"+
		"\59\35\2\u008d\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u008d\3\2\2\2\u008f"+
		"\u0090\3\2\2\2\u0090\u0097\3\2\2\2\u0091\u0093\7\60\2\2\u0092\u0094\5"+
		"9\35\2\u0093\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0093\3\2\2\2\u0095"+
		"\u0096\3\2\2\2\u0096\u0098\3\2\2\2\u0097\u0091\3\2\2\2\u0097\u0098\3\2"+
		"\2\2\u0098<\3\2\2\2\u0099\u009a\7^\2\2\u009a\u00ae\7$\2\2\u009b\u009c"+
		"\7^\2\2\u009c\u00ae\7^\2\2\u009d\u009e\7^\2\2\u009e\u00ae\7)\2\2\u009f"+
		"\u00a0\7^\2\2\u00a0\u00ae\7t\2\2\u00a1\u00a2\7^\2\2\u00a2\u00ae\7p\2\2"+
		"\u00a3\u00a4\7^\2\2\u00a4\u00ae\7v\2\2\u00a5\u00a6\7^\2\2\u00a6\u00a7"+
		"\7z\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\5\67\34\2\u00a9\u00aa\5\67\34"+
		"\2\u00aa\u00ab\5\67\34\2\u00ab\u00ac\5\67\34\2\u00ac\u00ae\3\2\2\2\u00ad"+
		"\u0099\3\2\2\2\u00ad\u009b\3\2\2\2\u00ad\u009d\3\2\2\2\u00ad\u009f\3\2"+
		"\2\2\u00ad\u00a1\3\2\2\2\u00ad\u00a3\3\2\2\2\u00ad\u00a5\3\2\2\2\u00ae"+
		">\3\2\2\2\u00af\u00b4\7$\2\2\u00b0\u00b3\5=\37\2\u00b1\u00b3\n\6\2\2\u00b2"+
		"\u00b0\3\2\2\2\u00b2\u00b1\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2"+
		"\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b7\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7"+
		"\u00c2\7$\2\2\u00b8\u00bd\7)\2\2\u00b9\u00bc\5=\37\2\u00ba\u00bc\n\6\2"+
		"\2\u00bb\u00b9\3\2\2\2\u00bb\u00ba\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb"+
		"\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00c0\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0"+
		"\u00c2\7)\2\2\u00c1\u00af\3\2\2\2\u00c1\u00b8\3\2\2\2\u00c2@\3\2\2\2\u00c3"+
		"\u00c5\t\7\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c4\3\2"+
		"\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\b!\2\2\u00c9"+
		"B\3\2\2\2\17\2v\u0085\u008f\u0095\u0097\u00ad\u00b2\u00b4\u00bb\u00bd"+
		"\u00c1\u00c6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}