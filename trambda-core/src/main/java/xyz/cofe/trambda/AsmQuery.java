package xyz.cofe.trambda;

import java.lang.invoke.SerializedLambda;
import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.log.api.Logger;

/**
 * Сериализация Java лямбды.
 *
 * <p>
 * Это клас по сути является абстрактным,
 * и для реализации конечной функциональности требуется переопределить метод {@link #call(Fn1, SerializedLambda, LambdaDump)}
 *
 * <p>
 * Данный класс по сути выполняет следующие функции
 *
 * <ol>
 *     <li>Метода {@link #apply(Fn1)} - получает байт код fn</li>
 *     <li>Полученный байт код передает в {@link #call(Fn1, SerializedLambda, LambdaDump)}</li>
 * </ol>
 *
 * <pre>
 * AtomicReference&lt;MethodDef&gt; mdefRef = new AtomicReference&lt;&gt;();
 * var res =
 *   new AsmQuery&lt;IEnv&gt;(){
 *       &#64;Override
 *       protected <RES> RES call(Fn&lt;IEnv, RES&gt; fn, SerializedLambda sl, MethodDef mdef){
 *           // Сохранение представления байт кода
 *           mdefRef.set(mdef);
 *           return super.call(fn, sl, mdef);
 *       }
 *   }.apply(
 *       env0 -&gt; env0.getUsers().filter(u -&gt; u.getName().contains("Petrov"))
 *   );
 * </pre>
 * @param <ENV> Окружение передаваемое в лямбду
 * @see MethodDump
 */
public class AsmQuery<ENV> implements Query<ENV> {
    private static final Logger log = Logger.of(AsmQuery.class);
    private static void log(String message,Object ... args){
        if( message==null )return;
        if( args==null || args.length==0 ){
            log.info(message);
        }else {
            log.info(message,args);
        }
    }

    protected final ThreadLocal<SerializedLambda> serLambda = new ThreadLocal<>();

    protected volatile LambdaDump lambdaDumpInst;
    protected LambdaDump lambdaDump(){
        if( lambdaDumpInst!=null )return lambdaDumpInst;
        synchronized( this ){
            if( lambdaDumpInst!=null )return lambdaDumpInst;
            lambdaDumpInst = new LambdaDump(){
                @Override
                protected void onSerializedLambda(SerializedLambda sl){
                    serLambda.set(sl);
                }
            };
            lambdaDumpInst.setCacheSerializedLambda(false);
            return lambdaDumpInst;
        }
    }

    /**
     * Сериализация и вызов лямбды
     * @param fn лямбда
     * @param <RES> результат вызова
     * @return результат вызова
     */
    @Override
    public <RES> RES apply(Fn1<ENV, RES> fn){
        if( fn==null )throw new IllegalArgumentException( "fn==null" );
        var dump = lambdaDump().dump(fn);
        return call( fn, serLambda.get(), dump);
    }

    /**
     * Реализация вызова лямбды
     * @param fn лямбда
     * @param sl лямбда - сериализация
     * @param dump байт-код лямбды
     * @param <RES> результат вызова
     * @return результат вызова
     */
    protected <RES> RES call( Fn1<ENV, RES> fn, SerializedLambda sl, LambdaDump dump ){
        return null;
    }
}
