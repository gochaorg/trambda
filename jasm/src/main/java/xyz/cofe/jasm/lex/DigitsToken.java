package xyz.cofe.jasm.lex;

import java.util.List;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.CharPointer;
import xyz.cofe.text.tparse.MapResultError;

public class DigitsToken extends CToken {
    public DigitsToken(CharPointer begin, CharPointer end) {
        super(begin, end);
    }
    public DigitsToken(CToken begin, CToken end) {
        super(begin, end);
    }
    public DigitsToken(List<CToken> tokens) {
        super(tokens);
    }
    protected DigitsToken(DigitsToken sample ){
        this( sample.begin(), sample.end() );
        this.radix = sample.radix;
    }

    public DigitsToken clone(){ return new DigitsToken(this); }

    private int radix = 10;
    public int radix() { return radix; }
    public DigitsToken radix(int radix){
        if( radix<1 )throw new IllegalArgumentException("radix<1");
        if( radix>digits.length() )throw new IllegalArgumentException("radix>"+digits.length());
        DigitsToken d = clone();
        d.radix = radix;
        return d;
    }

    public int length(){ return text().length(); }

    private static final String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final char[] charsOfDigits = digits.toCharArray();

    public int digit( int di ){
        if( di<0 )throw new IllegalArgumentException("di<0");
        if( di>=length() )throw new IllegalArgumentException("di>=length()");
        char c = Character.toLowerCase(text().charAt(di));
        for( int i=0; i<charsOfDigits.length; i++ ){
            if( charsOfDigits[i]==c ){
                return i;
            }
        }
        throw new MapResultError("input char "+text().charAt(di)+" not char of digits "+digits);
    }
}
