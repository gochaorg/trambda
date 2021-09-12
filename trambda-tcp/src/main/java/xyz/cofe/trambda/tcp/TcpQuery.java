package xyz.cofe.trambda.tcp;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import xyz.cofe.fn.Fn1;
import xyz.cofe.trambda.AsmQuery;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.log.api.Logger;

/**
 * Клиент для выполнения лямбд на сервере
 * <p>
 * Для создания простого клиента посмотрите описание сервера
 * {@link TcpServer}.
 * <p> Так же сервер может не допускать выполнение клиентского 
 * кода по соображениям безопасности. см {@link SecurError}
 * @param <ENV> Сервис который предоставляется сервером
 */
public class TcpQuery<ENV> extends AsmQuery<ENV> implements AutoCloseable {
    private static final Logger log = Logger.of(TcpQuery.class);

    /**
     * клиент TCP
     */
    protected final TcpClient client;

    /**
     * Возвращает ссылку на клиента TCP
     * @return клиент TCP
     */
    public TcpClient getClient(){ return client; }

    /**
     * Конструктор
     * @param client клиент TCP
     */
    public TcpQuery(TcpClient client){
        if( client==null )throw new IllegalArgumentException( "client==null" );
        this.client = client;
    }

    /**
     * Завершение работы
     * @throws Exception какие-то ошибки
     */
    @Override
    public void close() throws Exception {
        client.close();
    }

    /**
     * Создание клиента
     * @param c Сервис предоставляемый сервером
     * @param <ENV> Класс сервиса
     * @return Создатель клиента
     */
    public static <ENV> Builder<ENV> create(Class<ENV> c){
        return new Builder<>();
    }

    /**
     * Создатель клиента
     * @param <ENV> Класс сервиса
     */
    public static class Builder<ENV> {
        protected String host = "localhost";

        /**
         * Возвращает адрес расположения сервера
         * @return адрес расположения сервера
         */
        public String host(){ return this.host; }

        /**
         * Указывает адрес расположения сервера
         * @param address адрес расположения сервера
         * @return SELF ссылка
         */
        public Builder<ENV> host(String address){
            this.host = address;
            return this;
        }

        protected int port;

        /**
         * Возвращает порт расположения сервера
         * @return порт расположения сервера
         */
        public int port(){ return port; }

        /**
         * Указывает порт расположения сервера
         * @param port порт расположения сервера
         * @return SELF ссылка
         */
        public Builder<ENV> port(int port){
            this.port = port;
            return this;
        }

        /**
         * Создание клиента
         * @return клиент
         */
        public TcpQuery<ENV> build(){
            if( host==null )throw new IllegalStateException("host==nulll");
            try{
                Socket socket = new Socket(host,port);
                return new TcpQuery<ENV>( new TcpClient(socket) );
            } catch( IOException e ) {
                throw new IOError(e);
            }
        }
    }
    protected final Map<Fn1<?,?>,CompileResult> fnKeys = new ConcurrentHashMap<>();

    /**
     * Реализация вызова лямбды
     * @param fn лямбда
     * @param sl лямбда-сериализация
     * @param dump дамп лямбды
     * @param <RES> результат вызова
     * @return результат вызова
     */
    @Override
    protected <RES> RES call(Fn1<ENV, RES> fn, SerializedLambda sl, LambdaDump dump){
        var key = fnKeys.get(fn);
        if( key!=null ){
            return call(key,sl);
        }else{
            var ckey = client.compile(dump).fetch();
            fnKeys.put(fn,ckey);
            return call(ckey,sl);
        }
    }

    /**
     * Реализация вызова лямбды
     * @param key результат компиляции
     * @param sl лямбда-сериализация
     * @param <RES> результат вызова
     * @return результат вызова
     */
    protected <RES> RES call(CompileResult key,SerializedLambda sl){
        var execRes = client.execute(key).configure( exec -> {
            if( sl.getCapturedArgCount()>0 ){
                var args = new ArrayList<Object>();
                exec.setCapturedArgs(args);
                for( int ai=0;ai<sl.getCapturedArgCount();ai++ ){
                    args.add(sl.getCapturedArg(ai));
                }
            }else{
                exec.setCapturedArgs(null);
            }
        }).fetch();
        //noinspection unchecked
        return (RES)execRes.getValue();
    }

//    public <T> AutoCloseable subscribe(Consumer<T> consumer){
//        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
//
//        Consumer<ServerEvent> subl =  serverEvent -> {
//            var ev = serverEvent.getEvent();
//            //noinspection unchecked
//            T tEv = (T)ev;
//            consumer.accept(tEv);
//        };
//
//        var publisherName = "defaultPublisher";
//        var sub = client.subscribe(publisherName, subl).fetch();
//
//        return ()->{
//            client.unsubscribe(subl);
//        };
//    }

    private static final WeakHashMap<Publisher.Subscriber<?>, Consumer<ServerEvent>> subs =
        new WeakHashMap<>();

    /**
     * Подписка на события сервера, см {@link Subscribe}
     * @param cls Класс событий
     * @param <T> Класс событий
     * @return Интерфейс подписки
     */
    public <T> T subscribe( Class<T> cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        var pub = new PubProxy(){
            @SuppressWarnings("rawtypes")
            @Override
            protected Publisher<?> publisher(Method method){
                log.info("create Publisher proxy for {}", method);
                return new Publisher(){
                    @Override
                    public AutoCloseable listen(Subscriber subscriber){
                        if( subscriber==null )throw new IllegalArgumentException( "subscriber==null" );

                        Consumer<ServerEvent> subl =  serverEvent -> {
                            var ev = serverEvent.getEvent();

                            //noinspection unchecked
                            subscriber.notification(ev);
                        };

                        subs.put(subscriber, subl);

                        String publisherName = method.getName();
                        client.subscribe(publisherName, subl).fetch();

                        return () -> {
                            client.unsubscribe(subl);
                        };
                    }

                    @Override
                    public void removeListener(Subscriber subscriber){
                        var subl = subs.get(subscriber);
                        if( subl!=null ){
                            client.unsubscribe(subl);
                        }
                    }
                };
            }
        };
        return pub.proxy(cls);
    }

    /**
     * Подписка на события сервера, см {@link Subscribe}
     * @param cls Класс событий
     * @param publishers подписчик
     * @param <T> Класс событий
     * @return SELF ссылка
     */
    public <T> TcpQuery<ENV> subscribe(Class<T> cls, Consumer<T> publishers ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        if( publishers==null )throw new IllegalArgumentException( "publishers==null" );
        T pubs = subscribe(cls);
        publishers.accept(pubs);
        return this;
    }
}
