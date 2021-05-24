package xyz.cofe.jasm.lex;

import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public enum KeyWord {
    braceOpen("{"), braceClose("}"),
    parenthesisOpen("("), parenthesisClose(")"),
    bracketOpen("["), bracketClose("]"),
    colon(":"), semicolon(";"), comma(","),

    less("<"), more(">"),
    equals("=="), notEquals("!="),
    lessOrEquals("<="), moreOrEquals(">="),

    clazz("class"), _enum("enum", ACC_ENUM),
    asm("asm"), _var("var"), _throws("throws"),
    pkg("package"), _import("import"), as("as"),
    _extends("extends"), _implements("implements"),

    itf("interface", ACC_INTERFACE),

    i8("byte", true), i16("short", true), i32("int", true), i64("long", true),
    f32("float", true),f64("double", true),
    bool("boolean", true), chr("char", true),

    transitive("transitive", ACC_TRANSITIVE),
    _transient("transient", ACC_TRANSIENT),
    _final("final", ACC_FINAL),
    mandated("mandated", ACC_MANDATED),
    _abstract("abstract", ACC_ABSTRACT),
    annotation("annotation", ACC_ANNOTATION),
    deprecated("deprecated", ACC_DEPRECATED),
    module("module", ACC_MODULE),
    _native("native", ACC_NATIVE),
    open("open", ACC_OPEN),
    record("record", ACC_RECORD),
    _static("static", ACC_STATIC),
    staticPhase("staticPhase", ACC_STATIC_PHASE),
    strict("strict", ACC_STRICT),
    _super("strict", ACC_SUPER),
    varargs("varargs", ACC_VARARGS),
    _volatile("varargs", ACC_VOLATILE),
    bridge("bridge", ACC_BRIDGE),

    pub("public", ACC_PUBLIC),
    priv("private", ACC_PRIVATE),
    protect("protected", ACC_PROTECTED)
    ;

    public final String text;
    public final Optional<Integer> accFlag;
    public final boolean primitive;

    KeyWord( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        this.text = text;
        this.accFlag = Optional.empty();
        this.primitive = false;
    }
    KeyWord( String text, boolean primitive ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        this.text = text;
        this.accFlag = Optional.empty();
        this.primitive = primitive;
    }
    KeyWord( String text, int accFlag ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        this.text = text;
        this.accFlag = Optional.of(accFlag);
        this.primitive = false;
    }
}
