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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, ID=8, NUMBER=9, 
		STRING=10, WS=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "NameChar", "NameStartChar", 
			"ID", "HEX_DIGIT", "DIGIT", "NUMBER", "ESC", "STRING", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'*'", "'/'", "'+'", "'-'", "'('", "')'", "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "ID", "NUMBER", "STRING", 
			"WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\r\u0081\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2"+
		"\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\5\t\64\n\t\3"+
		"\n\3\n\3\13\3\13\7\13:\n\13\f\13\16\13=\13\13\3\f\3\f\3\r\3\r\3\16\6\16"+
		"D\n\16\r\16\16\16E\3\16\3\16\7\16J\n\16\f\16\16\16M\13\16\5\16O\n\16\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\5\17e\n\17\3\20\3\20\3\20\7\20j\n\20\f\20"+
		"\16\20m\13\20\3\20\3\20\3\20\3\20\7\20s\n\20\f\20\16\20v\13\20\3\20\5"+
		"\20y\n\20\3\21\6\21|\n\21\r\21\16\21}\3\21\3\21\2\2\22\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\2\23\2\25\n\27\2\31\2\33\13\35\2\37\f!\r\3\2\b\7\2\62"+
		";aa\u00b9\u00b9\u0302\u0371\u2041\u2042\17\2C\\c|\u00c2\u00d8\u00da\u00f8"+
		"\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1"+
		"\u3003\ud801\uf902\ufdd1\ufdf2\uffff\5\2\62;CHch\3\2\62;\6\2\f\f\17\17"+
		"$$^^\5\2\13\f\17\17\"\"\2\u008c\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\25\3\2\2\2\2\33\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\3#\3\2\2\2\5%\3\2\2\2\7\'\3\2\2\2\t)\3\2"+
		"\2\2\13+\3\2\2\2\r-\3\2\2\2\17/\3\2\2\2\21\63\3\2\2\2\23\65\3\2\2\2\25"+
		"\67\3\2\2\2\27>\3\2\2\2\31@\3\2\2\2\33C\3\2\2\2\35d\3\2\2\2\37x\3\2\2"+
		"\2!{\3\2\2\2#$\7,\2\2$\4\3\2\2\2%&\7\61\2\2&\6\3\2\2\2\'(\7-\2\2(\b\3"+
		"\2\2\2)*\7/\2\2*\n\3\2\2\2+,\7*\2\2,\f\3\2\2\2-.\7+\2\2.\16\3\2\2\2/\60"+
		"\7#\2\2\60\20\3\2\2\2\61\64\5\23\n\2\62\64\t\2\2\2\63\61\3\2\2\2\63\62"+
		"\3\2\2\2\64\22\3\2\2\2\65\66\t\3\2\2\66\24\3\2\2\2\67;\5\23\n\28:\5\21"+
		"\t\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<\26\3\2\2\2=;\3\2\2\2>?\t"+
		"\4\2\2?\30\3\2\2\2@A\t\5\2\2A\32\3\2\2\2BD\5\31\r\2CB\3\2\2\2DE\3\2\2"+
		"\2EC\3\2\2\2EF\3\2\2\2FN\3\2\2\2GK\7\60\2\2HJ\5\31\r\2IH\3\2\2\2JM\3\2"+
		"\2\2KI\3\2\2\2KL\3\2\2\2LO\3\2\2\2MK\3\2\2\2NG\3\2\2\2NO\3\2\2\2O\34\3"+
		"\2\2\2PQ\7^\2\2Qe\7$\2\2RS\7^\2\2Se\7^\2\2TU\7^\2\2Ue\7)\2\2VW\7^\2\2"+
		"We\7t\2\2XY\7^\2\2Ye\7p\2\2Z[\7^\2\2[e\7v\2\2\\]\7^\2\2]^\7z\2\2^_\3\2"+
		"\2\2_`\5\27\f\2`a\5\27\f\2ab\5\27\f\2bc\5\27\f\2ce\3\2\2\2dP\3\2\2\2d"+
		"R\3\2\2\2dT\3\2\2\2dV\3\2\2\2dX\3\2\2\2dZ\3\2\2\2d\\\3\2\2\2e\36\3\2\2"+
		"\2fk\7$\2\2gj\5\35\17\2hj\n\6\2\2ig\3\2\2\2ih\3\2\2\2jm\3\2\2\2ki\3\2"+
		"\2\2kl\3\2\2\2ln\3\2\2\2mk\3\2\2\2ny\7$\2\2ot\7)\2\2ps\5\35\17\2qs\n\6"+
		"\2\2rp\3\2\2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2\2\2vt\3\2"+
		"\2\2wy\7)\2\2xf\3\2\2\2xo\3\2\2\2y \3\2\2\2z|\t\7\2\2{z\3\2\2\2|}\3\2"+
		"\2\2}{\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\b\21\2\2\u0080\"\3\2\2"+
		"\2\17\2\63;EKNdikrtx}\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}