package xyz.cofe.jasm.lex;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.CharPointer;
import xyz.cofe.text.tparse.GR;
import xyz.cofe.text.tparse.Tokenizer;

import static xyz.cofe.text.tparse.Chars.*;

public class Lexer {
    public static final GR<CharPointer, IdToken> id = p -> {
        var begin = p;
        var c0 = begin.lookup(0);
        if( c0.isEmpty() )return Optional.empty();
        if( !c0.map( c ->
            Character.isLetter(c) ||
            c == '$' ||
            c == '_'
        ).get() )return Optional.empty();

        p = p.move(1);
        while( true ){
            var c1 = p.lookup(0);
            if( c1.isEmpty() )break;
            if( c1.map( c ->
                Character.isLetterOrDigit(c) ||
                    c == '$' ||
                    c == '_'
            ).get() ){
                p = p.move(1);
                continue;
            }
            break;
        }

        return Optional.of(new IdToken(begin,p));
    };
    public static final GR<CharPointer, WsToken> ws = whitespace.repeat().map(WsToken::new);
    public static final GR<CharPointer, DigitsToken> digits
        = digit.repeat().map(DigitsToken::new);

    public static final GR<CharPointer, NumberToken> integerNumber
        = digit.repeat().map( digits -> new NumberToken( new DigitsToken(digits) ) );

    public static final GR<CharPointer, CToken> dot = test(c -> c=='.' );

    public static final GR<CharPointer, NumberToken> floatNumber
        = digits.next(dot).next(digits)
        .map( (intDigits,dot,floatDigits)->new NumberToken(intDigits,floatDigits) );

    public static final GR<CharPointer, NumberToken> number
        = floatNumber.another(integerNumber)
        .map( t->(NumberToken)t );

    public static class ExpectKeyWords implements GR<CharPointer, KeyWordToken>{
        public final TreeSet<KeyWord> keyWords;

        public ExpectKeyWords(){
            this.keyWords = new TreeSet<KeyWord>( (a,b)->{
                int c0 = Integer.compare( b.text.length(), a.text.length() );
                if( c0!=0 )return c0;
                return a.text.compareTo(b.text);
            });
        }

        public ExpectKeyWords( Iterable<KeyWord> keyWords ){
            if( keyWords==null )throw new IllegalArgumentException( "keyWords==null" );
            this.keyWords = new TreeSet<KeyWord>( (a,b)->{
                int c0 = Integer.compare( b.text.length(), a.text.length() );
                if( c0!=0 )return c0;
                return a.text.compareTo(b.text);
            });
            for( var k : keyWords ){
                if( k!=null ){
                    this.keyWords.add(k);
                }
            }
        }

        @Override
        public Optional<KeyWordToken> apply(CharPointer p){
            if( p==null )throw new IllegalArgumentException( "p==null" );

            var b = p;
            if( p.eof() )return Optional.empty();

            for( var kw : this.keyWords ){
                var str = kw.text;
                boolean matched = true;
                for( var ci=0; ci<str.length(); ci++ ){
                    var c = p.lookup(ci);
                    if( c.isEmpty() ){
                        matched = false;
                        break;
                    }

                    if( !c.get().equals(str.charAt(ci)) ){
                        matched = false;
                        break;
                    }
                }
                if( matched ){
                    return Optional.of(
                        new KeyWordToken(b,b.move(str.length()), kw)
                    );
                }
            }

            return Optional.empty();
        }
    }

    public static ExpectKeyWords keyWords( KeyWord k0, KeyWord ... kN ){
        if( k0==null )throw new IllegalArgumentException( "k0==null" );
        if( kN==null )throw new IllegalArgumentException( "kN==null" );

        ExpectKeyWords kw = new ExpectKeyWords();
        kw.keyWords.add(k0);
        kw.keyWords.addAll(Arrays.asList(kN));
        return kw;
    }
    public static ExpectKeyWords keyWords = new ExpectKeyWords();
    static {
        keyWords.keyWords.addAll( Arrays.asList(KeyWord.values()) );
    }

    public static final GR<CharPointer, CommentToken> singleLineComment1 = p -> {
        if( p.eof() )return Optional.empty();

        var c0 = p.lookup(0);
        if( c0.isEmpty() )return Optional.empty();
        if( !c0.get().equals('#') )return Optional.empty();

        var b = p;
        int i = 0;
        while( true ){
            i++;
            var c1 = p.lookup(i);
            if( c1.isEmpty() )break;
            if( c1.get().equals('\n') ){
                var c2 = p.lookup(i+1);
                if( c2.map(c -> c.equals('\r')).orElse(false) ){
                    i += 2;
                    break;
                }
                i += 1;
                break;
            }
            if( c1.get().equals('\r') ){
                var c2 = p.lookup(i+1);
                if( c2.map(c -> c.equals('\n')).orElse(false) ){
                    i += 2;
                    break;
                }
                i += 1;
                break;
            }
        }

        return Optional.of(
            new CommentToken(b, b.move(i))
        );
    };
    public static final GR<CharPointer, CommentToken> comment = singleLineComment1;

    public static Tokenizer<CharPointer, ? extends CToken> tokenizer( String source ){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        //noinspection unchecked
        return Tokenizer.lexer( source,
            ws, comment,
            keyWords,
            number,
            dot,
            id
        );
    }

    public static List<? extends CToken> tokens( String source ){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        var toks = tokenizer(source);
        return toks
            .filter( t -> !(t instanceof WsToken || t instanceof CommentToken) )
            .toList();
    }
}
