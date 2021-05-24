package xyz.cofe.jasm.lex;

import java.util.Optional;
import xyz.cofe.text.tparse.CToken;

public class NumberToken extends CToken {
    private final DigitsToken integerPart;
    private final DigitsToken floatPart;

    public NumberToken(DigitsToken integerPart){
        super(integerPart.begin(), integerPart.end());
        this.integerPart = integerPart;
        this.floatPart = null;
        if( integerPart == null ) throw new IllegalArgumentException("integerPart == null");
    }

    public NumberToken(DigitsToken integerPart, DigitsToken floatPart){
        super(integerPart.begin(), floatPart != null ? floatPart.end() : integerPart.end());
        this.integerPart = integerPart;
        this.floatPart = floatPart;
        if( integerPart == null ) throw new IllegalArgumentException("integerPart == null");
    }

    public DigitsToken integerDigits(){
        return integerPart;
    }

    public Optional<DigitsToken> floatDigits(){
        return integerPart == null ? Optional.empty() : Optional.of(integerPart);
    }

    public boolean isFloat(){
        return floatPart != null;
    }

    private Long longValue;

    public long longValue(){
        if( longValue != null ) return longValue;
        long v = 0;
        long k = 1;
        for( int i = 0; i < integerPart.length(); i++ ){
            long d = integerPart.digit(integerPart.length() - 1 - i);
            d = d * k;
            k = k * integerPart.radix();
            v = v + d;
        }
        longValue = v;
        return longValue;
    }

    private Double floatPartValue;

    protected double floatPartValue(){
        if( floatPartValue != null ) return floatPartValue;
        if( floatPart == null ){
            floatPartValue = 0.0;
            return floatPartValue;
        }
        double v = 0.0;
        double k = 1.0 / floatPart.radix();
        for( int i = 0; i < floatPart.length(); i++ ){
            double d = floatPart.digit(i);
            v = v + d * k;
            k = k / floatPart.radix();
        }
        floatPartValue = v;
        return floatPartValue;
    }

    public double doubleValue(){
        double lngv = longValue();
        double fltv = floatPartValue();
        return lngv + fltv;
    }

    @Override
    public String toString(){
        return NumberToken.class.getSimpleName() + " " + doubleValue();
    }
}
