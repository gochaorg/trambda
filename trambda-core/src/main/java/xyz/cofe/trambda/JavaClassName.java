package xyz.cofe.trambda;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Кодирование имени класса
 */
public class JavaClassName {
    /**
     * Конструктор для byte-code названия класса
     * @param rawName байт-код название класса,
     *                т.е. класс <code>java.lang.String</code> в данном случае будет <code>java/lang/String</code>
     */
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

    /**
     * Конструктор
     * @param name полное имя класса, например <code>java.lang.String</code>
     * @param simpleName простое имя класса (без имени пакета), например <code>String</code>
     * @param packageName имя пакета содержащий класс <code>java.lang</code>
     */
    public JavaClassName(String name, String simpleName, String packageName ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( simpleName==null )throw new IllegalArgumentException( "simpleName==null" );
        if( packageName==null )throw new IllegalArgumentException( "packageName==null" );
        this.name = name;
        this.simpleName = simpleName;
        this.packageName = packageName;
    }

    /**
     * Полное имя класса, например <code>java.lang.String</code>
     */
    public final String name;

    /**
     * Простое имя класса, например, например <code>String</code>
     */
    public final String simpleName;

    /**
     * Имя пакета содержащий класс, например <code>java.lang</code>
     */
    public final String packageName;

    /**
     * Возвращает byte-code названия класса
     * @return т.е. класс <code>java.lang.String</code> в данном случае будет <code>java/lang/String</code>
     */
    public String rawName(){ return name.replace(".","/"); }

    /**
     * Клонирует и меняет имя класса
     * @param name байт-код название
     * @return клон копия
     */
    public JavaClassName withName( String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return new JavaClassName( name );
    }
    public static final Pattern validId = Pattern.compile("(?is)[\\w$_][\\w\\d$_]*");

    /**
     * Клонирует и меняет простое название класса, с сохранением имени пакета
     * @param name простое название класса (без имени пакета)
     * @return клон копия
     */
    public JavaClassName withSimpleName( String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( !validId.matcher(name).matches() ){
            throw new IllegalArgumentException( "name not match "+validId );
        }
        return new JavaClassName(name, name, packageName);
    }

    /**
     * Клонирует и меняет простое имя пакета, с сохранением имени простого имени
     * @param pkgName имя пакета
     * @return клон копия
     */
    public JavaClassName withPackage( String pkgName ){
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
