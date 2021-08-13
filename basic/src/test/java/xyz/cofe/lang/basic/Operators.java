package xyz.cofe.lang.basic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Operators {
    public static class Fun {
        public final String name;
        public final Method method;
        public final MethodHandle handle;

        public Fun(String name, Method method, MethodHandle handle) {
            this.name = name;
            this.method = method;
            this.handle = handle;
        }

        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(name).append("(");
            var params = method.getParameters();
            for( int i=0;i<params.length; i++ ){
                if( i>0 )sb.append(", ");
                var p = params[i];
                sb.append(p.getName()).append(":").append(p.getType().getName());
            }
            sb.append(")");
            sb.append(":").append(method.getGenericReturnType().getTypeName());
            return sb.toString();
        }
    }

    public final List<Fun> functions;
    {
        List<Fun> funs = new ArrayList<>();
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        var cls = this.getClass();
        for( var meth : cls.getMethods() ){
            var exp = meth.getAnnotation(export.class);
            if( exp==null )continue;

            MethodHandle h = null;
            try {
                h = publicLookup.unreflect(meth);
                h = h.bindTo(this);
                var fun = new Fun(meth.getName(), meth, h);
                funs.add(fun);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        functions = Collections.unmodifiableList(funs);
    }

    public static class ArgTypeCompare {
        public final Class actual;
        public final Class expected;

        public ArgTypeCompare(Class actual, Class expected) {
            this.actual = actual;
            this.expected = expected;
        }

        public boolean sameTypes(){ return actual==expected; }
        public boolean passable(){
            //noinspection unchecked
            return actual.isAssignableFrom(expected);
        }
        public boolean contrVariantPass(){
            return !sameTypes() && passable();
        }
    }
    public static class Target {
        public final Fun fun;
        public final Class[] expectArgs;

        public Target(Fun fun, Class[] expectArgs) {
            if( fun==null )throw new IllegalArgumentException( "fun==null" );
            if( expectArgs==null )throw new IllegalArgumentException( "expectArgs==null" );
            if( fun.method.getParameterCount()!=expectArgs.length )throw new IllegalArgumentException( "fun.method.getParameterCount()!=expectArgs.length" );
            this.fun = fun;
            this.expectArgs = expectArgs;
        }

        protected List<ArgTypeCompare> argCompares;
        public List<ArgTypeCompare> argCompares(){
            if( argCompares!=null )return argCompares;
            argCompares = new ArrayList<>();
            for(var i=0; i<expectArgs.length; i++ ){
                var e = expectArgs[i];
                var a = fun.method.getParameterTypes()[i];
                argCompares.add(new ArgTypeCompare(a,e));
            }
            return argCompares;
        }

        protected Integer contrVarCalls;
        public int contrVarCalls(){
            if( contrVarCalls!=null )return contrVarCalls;
            contrVarCalls = (int)(argCompares().stream().filter( a -> a.contrVariantPass() ).count());
            return contrVarCalls;
        }

        public Object invoke( Object... args ){
            try {
                return fun.handle.invokeWithArguments(args);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
    public static class Found {
        public final List<Target> targets;
        public Found(List<Target> targets) {
            this.targets = Collections.unmodifiableList(targets);
        }

        public boolean isEmpty(){ return targets.isEmpty(); }
        public int size(){ return targets.size(); }
        public Target get(int i){ return targets.get(i); }

        public Optional<Target> preffered(){
            if( targets.isEmpty() )return Optional.empty();
            if( targets.size()==1 )return Optional.of(targets.get(0));

            List<Target> ls = new ArrayList<>(targets);
            ls.sort( (a,b)->Integer.compare(a.contrVarCalls(), b.contrVarCalls()) );
            var min = ls.get(0).contrVarCalls();

            List<Target> res = ls.stream().filter(a -> a.contrVarCalls()==min).collect(Collectors.toList());
            if( res.size()>1 ){
                System.err.println(
                    "ambiguous call:\n" +
                    res.stream().map( t -> t.fun.toString() ).reduce((a,b)->a+"\n"+b).get()
                );
                return Optional.empty();
            }

            return Optional.of(res.get(0));
        }
    }
    public Found find( String name, Class ... args ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( args==null )throw new IllegalArgumentException( "args==null" );
        List<Target> targets = new ArrayList<>();
        for( var f : functions ){
            if( !f.name.equals(name) )continue;
            if( f.method.getParameters().length!=args.length )continue;
            if( args.length==0 ){
                targets.add(new Target(f,args));
            }else{
                boolean assign = true;
                for( var i=0;i<args.length;i++ ){
                    var e = f.method.getParameterTypes()[i];
                    var a = args[i];
                    if( !e.isAssignableFrom(a) ){
                        assign = false;
                        break;
                    }
                }
                if( assign ) {
                    targets.add(new Target(f, args));
                }
            }
        }
        return new Found(targets);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface export {}

    @export public Double add( Double a, Double b ){ return a + b; }
    @export public Double add( Double a, Number b ){ return a + b.doubleValue(); }
    @export public Double add( Number a, Number b ){ return a.doubleValue() + b.doubleValue(); }

    @export public NullRef add( Number a, NullRef b ){ return null; }
    @export public NullRef add( NullRef a, Number b ){ return null; }
    @export public Object add( NullRef a, NullRef b ){ return null; }

    @export public String add( String a, Object b ){ return a + b; }
    @export public String add( Object a, String b ){ return a + b; }
    @export public String add( String a, String b ){ return a + b; }
    @export public String add( String a, NullRef b ){ return a + null; }
    @export public String add( NullRef a, String b ){ return null + b; }

    @export public Double sub( Double a, Double b ){ return a - b; }
    @export public Double sub( Double a, Number b ){ return a - b.doubleValue(); }
    @export public Double sub( Number a, Number b ){ return a.doubleValue() - b.doubleValue(); }

    @export public Double mul( Double a, Double b ){ return a * b; }
    @export public Double mul( Double a, Number b ){ return a * b.doubleValue(); }
    @export public Double mul( Number a, Number b ){ return a.doubleValue() * b.doubleValue(); }

    @export public Double div( Double a, Double b ){ return a / b; }
    @export public Double div( Double a, Number b ){ return a.doubleValue() / b.doubleValue(); }
    @export public Double div( Number a, Number b ){ return a.doubleValue() / b.doubleValue(); }
}
