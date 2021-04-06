package xyz.cofe.trambda.sec;

public class TypeDesc {
    public static final TypeDesc undefined = new TypeDesc("?", 0);

    public final String name;
    public final int dimension;
    public final boolean array;

    public TypeDesc(String name, int dimension){
        if( name == null ) throw new IllegalArgumentException("name==null");
        if( dimension < 0 ) throw new IllegalArgumentException("dimension<0");
        this.name = name;
        this.dimension = dimension;
        array = dimension>0;
    }
    public TypeDesc(String name){
        this(name,0);
    }

    public static class Parse {
        public final TypeDesc type;
        public final int from;
        public final int to;
        public Parse(TypeDesc type, int from, int to){
            this.type = type;
            this.from = from;
            this.to = to;
        }
    }
    public static Parse parse(String raw,int from){
        if( raw==null )throw new IllegalArgumentException( "raw==null" );
        if( from<0 )throw new IllegalArgumentException( "from<0" );
        if( from>=raw.length() )throw new IllegalArgumentException( "from>=raw.length()" );

        int ptr = from-1;
        int arr = 0;
        String type = null;
        int state = 0;
        StringBuilder typeName = new StringBuilder();
        while( ptr<(raw.length()-1) && type==null ){
            ptr++;
            char c = raw.charAt(ptr);
            switch( state ){
                case 0:
                    switch( c ){
                        case '[':
                            arr++;
                            continue;
                        case 'Z':
                            type = boolean.class.getName();
                            break;
                        case 'C':
                            type = char.class.getName();
                            break;
                        case 'B':
                            type = byte.class.getName();
                            break;
                        case 'S':
                            type = short.class.getName();
                            break;
                        case 'I':
                            type = int.class.getName();
                            break;
                        case 'F':
                            type = float.class.getName();
                            break;
                        case 'J':
                            type = long.class.getName();
                            break;
                        case 'D':
                            type = double.class.getName();
                            break;
                        case 'V':
                            type = void.class.getName();
                            break;
                        case 'L':
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    switch( c ){
                        case ';':
                            type = typeName.toString();
                            break;
                        case '/':
                        case '.':
                            typeName.append(".");
                            break;
                        default:
                            typeName.append(c);
                            break;
                    }
                    break;
            }
        }

        if( type==null )throw new Error("can't parse for \""+raw+"\", from="+from);

        return new Parse(
            new TypeDesc(type,arr)
            ,from,ptr+1);
    }
    public static TypeDesc parse(String raw){
        if( raw==null )throw new IllegalArgumentException( "raw==null" );
        return parse(raw,0).type;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if( dimension>0 ){
            for( int d = 0; d < dimension; d++ ){
                sb.append("[]");
            }
        }
        return sb.toString();
    }
}
