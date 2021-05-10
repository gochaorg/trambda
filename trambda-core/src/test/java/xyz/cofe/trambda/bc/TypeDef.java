package xyz.cofe.trambda.bc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import xyz.cofe.trambda.Tuple2;

public class TypeDef {
    private final List<TypeDef> input;
    private final TypeDef output;
    private final List<TypeDef> genericParams;

    private final String name;
    private final boolean primitive;
    private final int dimension;
    private final boolean voidType;

    public TypeDef(TypeDef sample, int dimension){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        List<TypeDef> input1 = null;
        if( sample.input!=null ){
            input1 = new ArrayList<>();
            for( var a : sample.input ){
                input1.add(a!=null ? new TypeDef(a,a.dimension) : null);
            }
        }
        input = input1!=null ? Collections.unmodifiableList(input1) : List.of();

        List<TypeDef> gen1 = null;
        if( sample.genericParams!=null ){
            gen1 = new ArrayList<>();
            for( var a : sample.genericParams ){
                gen1.add( a!=null ? new TypeDef(a, a.dimension) : null);
            }
        }
        genericParams = gen1!=null ? Collections.unmodifiableList(gen1) : List.of();

        this.dimension = dimension;
        this.output = sample.output;

        this.name = sample.name;
        this.primitive = sample.primitive;
        this.voidType = sample.voidType;
    }
    public TypeDef(String name, boolean primitive, boolean voidType, int dimension, List<TypeDef> genericParams){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( genericParams==null )throw new IllegalArgumentException( "genericParams==null" );
        if( dimension<0 )throw new IllegalArgumentException( "dimension<0" );
        this.name = name;
        this.primitive = primitive;
        this.voidType = voidType;
        this.dimension = dimension;
        this.genericParams = genericParams;
        output = VOID;
        input = List.of();
    }
    public TypeDef(String name, boolean primitive, int dimension, List<TypeDef> genericParams){
        this(name,primitive,false,dimension,genericParams);
    }
    public TypeDef(String name, boolean primitive, int dimension){
        this(name,primitive,false,dimension,List.of());
    }
    public TypeDef(String name, boolean primitive){
        this(name,primitive,false,0,List.of());
    }
    public TypeDef(String name, int dimension, List<TypeDef> genericParams){
        this(name,false,false,dimension,genericParams);
    }
    public TypeDef(List<TypeDef> input, TypeDef output){
        if( input==null )throw new IllegalArgumentException( "input==null" );
        if( output==null )throw new IllegalArgumentException( "output==null" );

        this.input = Collections.unmodifiableList(new ArrayList<>(input));
        this.output = output;
        this.dimension = 0;
        this.genericParams = List.of();
        this.name = "";
        this.voidType = false;
        this.primitive = false;
    }

    public String getName(){
        return name.replace("/",".");
    }
    public boolean isPrimitive(){ return primitive; }
    public boolean isVoid(){ return voidType; }
    public int getDimension(){ return dimension; }
    public List<TypeDef> getInput(){ return input; }
    public TypeDef getOutput(){ return output; }
    public List<TypeDef> getGenericParams(){ return genericParams; }
    public boolean isArray(){ return dimension>0; }

    public TypeDef dimension(int dimension){
        TypeDef t = new TypeDef(this,dimension);
        return t;
    }

    public static final TypeDef VOID = new TypeDef( void.class.getName(), true, true, 0, List.of() );
    public static final TypeDef BOOLEAN = new TypeDef( boolean.class.getName(), true );
    public static final TypeDef CHAR = new TypeDef( char.class.getName(), true );
    public static final TypeDef BYTE = new TypeDef( byte.class.getName(), true );
    public static final TypeDef SHORT = new TypeDef( short.class.getName(), true );
    public static final TypeDef INT = new TypeDef( int.class.getName(), true );
    public static final TypeDef LONG = new TypeDef( long.class.getName(), true );
    public static final TypeDef FLOAT = new TypeDef( float.class.getName(), true );
    public static final TypeDef DOUBLE = new TypeDef( double.class.getName(), true );

    @Override
    public boolean equals(Object o){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        TypeDef typeDef = (TypeDef) o;
        return primitive == typeDef.primitive
            && dimension == typeDef.dimension
            && voidType == typeDef.voidType
            && input.equals(typeDef.input)
            && output.equals(typeDef.output)
            && genericParams.equals(typeDef.genericParams)
            && name.equals(typeDef.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(input, output, genericParams, name, primitive, dimension, voidType);
    }

    public static Optional<Tuple2<TypeDef,Integer>> parseOne(String str, int off){
        if( off<0 )return Optional.empty();
        if( off>=str.length() )return Optional.empty();

        int p = off-1;
        int arr = 0;
        int state = 0;
        TypeDef t = null;
        StringBuilder buff = new StringBuilder();
        List<TypeDef> genericParams = new ArrayList<>();

        while( p<str.length() ){
            p++;
            char c0 = str.charAt(p);
            switch( state ){
                case 0:
                    switch( c0 ){
                        case '[':
                            arr++;
                            break;
                        case 'Z':
                            t = BOOLEAN;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'C':
                            t = CHAR;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'B':
                            t = BYTE;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'S':
                            t = SHORT;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'I':
                            t = INT;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'F':
                            t = FLOAT;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'J':
                            t = LONG;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'D':
                            t = DOUBLE;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'V':
                            t = VOID;
                            if( arr>0 )t=t.dimension(arr);
                            return Optional.of(Tuple2.of(t,p+1));
                        case 'L':
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch( c0 ){
                        case ';':
                            t = new TypeDef(buff.toString(),false,false,arr,genericParams);
                            return Optional.of(Tuple2.of(t,p+1));
                        case '<':
                            var from = p+1;
                            int next = 0;
                            while( true ){
                                var gp = parseOne(str, from);
                                if( gp.isEmpty() ) return Optional.empty();

                                genericParams.add(gp.get().a());
                                next = gp.get().b();
                                boolean repeat = false;
                                if( next < str.length() && str.substring(next).startsWith(">") ){
                                    repeat = false;
                                    next = next+1;
                                } else {
                                    repeat = true;
                                    from = next;
                                }

                                if( !repeat )break;
                            }
                            p = next-1;
                            break;
                        default:
                            buff.append(c0);
                            break;
                    }
                    break;
            }
        }

        return Optional.empty();
    }
    public static Optional<Tuple2<TypeDef,Integer>> parse(String str,int off){
        if( off<0 )return Optional.empty();
        if( off>=str.length() )return Optional.empty();

        if( !str.substring(off).startsWith("(") ){
            return parseOne( str,off );
        }

        off++;

        List<TypeDef> input = new ArrayList<>();
        while( off<str.length() ){
            char c0 = str.charAt(off);
            if( c0==')' ){
                off++;
                break;
            } else {
                var p = parseOne(str,off);
                if( p.isEmpty() )return Optional.empty();
                input.add(p.get().a());
                off = p.get().b();
            }
        }

        var r = parseOne(str,off);
        if( r.isEmpty() )return Optional.empty();

        var t = new TypeDef(input, r.get().a());
        return Optional.of(Tuple2.of(t,r.get().b()));
    }

    public String toString(){
        if( input.size()>0 ){
            StringBuilder sb = new StringBuilder();
            sb.append("[".repeat(dimension));
            sb.append("(");
            for( var p : input ){
                sb.append(p);
            }
            sb.append(")");
            sb.append(output);
            return sb.toString();
        }

        if( isVoid() )
            return "[".repeat(dimension)+"V";

        if( isPrimitive() ){
            if( void.class.getName().equals(name) )return "[".repeat(dimension) + "V";
            if( double.class.getName().equals(name) )return "[".repeat(dimension) + "D";
            if( long.class.getName().equals(name) )return "[".repeat(dimension) + "J";
            if( float.class.getName().equals(name) )return "[".repeat(dimension) + "F";
            if( int.class.getName().equals(name) )return "[".repeat(dimension) + "I";
            if( short.class.getName().equals(name) )return "[".repeat(dimension) + "S";
            if( byte.class.getName().equals(name) )return "[".repeat(dimension) + "B";
            if( char.class.getName().equals(name) )return "[".repeat(dimension) + "C";
            if( boolean.class.getName().equals(name) )return "[".repeat(dimension) + "Z";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[".repeat(dimension));
            sb.append("L");
            sb.append(name);
            if( !genericParams.isEmpty() ){
                sb.append("<");
                for( var p : genericParams ){
                    sb.append(p.toString());
                }
                sb.append(">");
            }
            sb.append(";");
            return sb.toString();
        }

        return "?";
    }

    public String toJavaTypeName(){
        if( input.size()>0 ){
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            int i = -1;
            for( var p : input ){
                i++;
                if( i>0 )sb.append(",");
                sb.append(p.toJavaTypeName());
            }
            sb.append(")=>");
            sb.append(output.toJavaTypeName());

            sb.append("[".repeat(dimension));
            sb.append("]".repeat(dimension));
            return sb.toString();
        }

        String arrSuf = ("[".repeat(dimension))+("]".repeat(dimension));

        if( isVoid() )return void.class.getName()+arrSuf;

        if( isPrimitive() ){
            return getName()+arrSuf;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(getName());
            if( !genericParams.isEmpty() ){
                sb.append("<");
                int i = -1;
                for( var p : genericParams ){
                    i++;
                    if( i>0 )sb.append(",");
                    sb.append(p.toJavaTypeName());
                }
                sb.append(">");
            }
            return sb.toString();
        }
    }
}
