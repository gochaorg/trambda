package xyz.cofe.trambda;

import java.util.Objects;
import java.util.regex.Pattern;

public class JavaClassName {
    public JavaClassName(String rawName){
        if( rawName==null )throw new IllegalArgumentException( "rawName==null" );
        name = rawName.replace("/",".");

        String[] names = name.split("\\.");
        simpleName = names[names.length-1];

        if( names.length>1 ){
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<names.length-1; i++ ){
                if( i>0 )sb.append(".");
                sb.append(names[i]);
            }
            packageName = sb.toString();
        }else{
            packageName = "";
        }
    }

    public JavaClassName(String name, String simpleName, String packageName ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( simpleName==null )throw new IllegalArgumentException( "simpleName==null" );
        if( packageName==null )throw new IllegalArgumentException( "packageName==null" );
        this.name = name;
        this.simpleName = simpleName;
        this.packageName = packageName;
    }

    public final String name;
    public final String simpleName;
    public final String packageName;

    public String rawName(){ return name.replace(".","/"); }

    public JavaClassName withName(String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return new JavaClassName( name );
    }
    public static final Pattern validId = Pattern.compile("(?is)[\\w$_][\\w\\d$_]*");
    public JavaClassName withSimpleName(String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( !validId.matcher(name).matches() ){
            throw new IllegalArgumentException( "name not match "+validId );
        }
        return new JavaClassName(name, name, packageName);
    }
    public JavaClassName withPackage(String pkgName ){
        if( pkgName==null )throw new IllegalArgumentException( "pkgName==null" );
        if( pkgName.length()<1 ){
            return new JavaClassName(simpleName,simpleName,packageName);
        }

        String[] pnames = pkgName.split("\\.");
        for( String pname : pnames ){
            if( pname.length()<1 ){
                throw new IllegalArgumentException("pkgName part empty");
            }
            if( !validId.matcher(pname).matches() )
                throw new IllegalArgumentException("pkgName part "+pname+" not match "+validId);
        }

        return new JavaClassName( pkgName+"."+simpleName, simpleName, packageName );
    }

    @Override
    public boolean equals(Object o){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        JavaClassName javaClassName = (JavaClassName) o;
        return Objects.equals(name, javaClassName.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    public String toString(){ return name; }
}
