package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;

/**
 * Создание фильтров безопасности.
 * 
 * <p>
 * Пример:
  <pre>
import xyz.cofe.trambda.sec.SecurAccess;
import xyz.cofe.trambda.sec.SecurityFilters;

 ...
 server = new TcpServer&lt;IEnv&gt;(
    ssocket,
    session -&gt; new LinuxEnv(),
    // Задаем ряд фильтров которые проверяют на безопасный код
    // Фильтры проверяют байт-код на наличие определенных инструкций:
    // Вызов метода или чтение/изменение поля
    SecurityFilters.create(s -&gt; {
        // Фильтры деляться на две группы: 
        //   {@link SecurityFilters.Builder#allow(java.util.function.Consumer) allow() - разрешающие} 
        //   и {@link SecurityFilters.Builder#allow(java.util.function.Consumer) deny() - запрещающие}
        // Каждая группа может указана несколько раз
        // Фильтры применяются в порядке их указания
        // Применяется первый совпавший фильтр
        s.allow( a -&gt; { // Здесь задаются разрешающие вызовы
            // Проверяет вызов методов публичного сервиса
            a.invoke("demo api",              
              c-&gt;c.getOwner()
                .matches("xyz\\.cofe\\.trambda\\.tcp\\.demo\\.([\\w\\d]+)"));

            // Так же разрешено вызывать методы Java Collection
            a.invoke("java collections api", c-&gt;c.getOwner().matches(
                "java\\.util\\.(List)|java\\.util\\.stream\\.([\\w\\d]+)"));
                
            // Это разрешение на вызов методов класса String
            a.invoke("java lang api", c-&gt;c.getOwner().matches("java\\.lang\\.(String)"));
            
            // Это разрешение на вызов стандартных BootStrap методов, 
            // которые генериурует javaс
            a.invoke("java compiler", c-&gt;c.getOwner().matches("java\\.lang\\.invoke\\.(LambdaMetafactory|StringConcatFactory)"));
        });
        
        // Все остальные вызовы запрещены
        s.deny().any("by default");
    })
);
 </pre>
 * @param <MESSAGE> Сообщение генерируемое фильтром
 * @param <SCOPE> Область проверяемых данных
 */
public class SecurityFilters<MESSAGE,SCOPE> implements SecurityFilter<MESSAGE,SCOPE> {
    private final List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators;
    
    /**
     * Конструктор
     * @param validators фильтры безопасности
     */
    public SecurityFilters(List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators){
        List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators00 = new ArrayList<>();
        if( validators!=null ){
            validators00.addAll(validators);
        }
        this.validators = Collections.unmodifiableList(validators00);
    }

    /**
     * Проверка инструкций на предмет безопасности
     * @param secur инструкции
     * @return отчет о безопасности вызовов
     */
    public List<SecurMessage<MESSAGE,SCOPE>> validate( List<SecurAccess<?,SCOPE>> secur ){
        if( secur==null )throw new IllegalArgumentException( "secur==null" );

        List<SecurMessage<MESSAGE,SCOPE>> res = new ArrayList<>();
        var validtrs = validators;
        if( validtrs!=null ){
            for( var sa : secur ){
                if( sa==null )continue;

                MESSAGE msg = null;
                Boolean allw = null;
                for( var vald : validtrs ){
                    if( vald==null )continue;
                    var omsg = vald.apply(sa);
                    if( omsg.isPresent() ){
                        msg = omsg.get().a();
                        allw = omsg.get().b();
                        break;
                    }
                }

                if( msg!=null ){
                    res.add(new SecurMessage<>(sa,allw,msg));
                }
            }
        }

        return res;
    }
    
    
    /**
     * Создание {@link Builder}
     * @param <MESSAGE> Класс сообщений
     * @param <SCOPE> Область проверки
     * @param cMsg Класс сообщений
     * @param cScope Область проверки
     * @return Cтроитель фильтров
     */
    public static <MESSAGE,SCOPE> Builder<MESSAGE,SCOPE> create(Class<MESSAGE> cMsg, Class<SCOPE> cScope){
        return new Builder<>();
    }    
    
    /**
     * Создание {@link Builder}
     * @return Cтроитель фильтров
     */
    public static Builder<String, Tuple2<LambdaDump, LambdaNode>> create(){
        return create(null, null);
    }
    
    /**
     * Создание списка фильтров
     * @param conf конфигурация фильтров
     * @return фильтр безопасности
     */
    public static SecurityFilters<String, Tuple2<LambdaDump, LambdaNode>> create(Consumer<Builder<String, Tuple2<LambdaDump, LambdaNode>>> conf ){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        var bld = create();
        conf.accept(bld);
        return bld.build();
    }

    /**
     * Строитель фильтров
     * @param <MESSAGE> Класс сообщений
     * @param <SCOPE> Область проверки
     */
    public static class Builder<MESSAGE,SCOPE> {
        private final List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators = new ArrayList<>();

        /**
         * Создание фильтра
         * @return фильтр
         */
        public SecurityFilters<MESSAGE,SCOPE> build(){
            return new SecurityFilters<>(validators);
        }

        /**
         * Создание разрешающего фильтра
         * @return Строитель
         */
        public AllowBuilder<MESSAGE,SCOPE> allow(){
            return new AllowBuilder<>(this);
        }

        /**
         * Создание разрешающего фильтра
         * @param conf создание фильтра
         * @return SELF ссылку
         */
        public Builder<MESSAGE,SCOPE> allow(Consumer<AllowBuilder<MESSAGE,SCOPE>> conf){
            if( conf==null )throw new IllegalArgumentException( "conf==null" );
            conf.accept(new AllowBuilder<>(this));
            return this;
        }

        /**
         * Создание запрещающего фильтра
         * @return Строитель
         */
        public DenyBuilder<MESSAGE,SCOPE> deny(){
            return new DenyBuilder<>(this);
        }

        /**
         * Создание запрещающего фильтра
         * @param conf создание фильтра
         * @return SELF ссылку
         */
        public Builder<MESSAGE,SCOPE> deny(Consumer<DenyBuilder<MESSAGE,SCOPE>> conf){
            if( conf==null )throw new IllegalArgumentException( "conf==null" );
            conf.accept( new DenyBuilder<>(this) );
            return this;
        }
    }

    /**
     * Строитель фильтра-предиката
     * @param <MESSAGE> Класс сообщений
     * @param <SCOPE> Область проверки
     * @param <SELF> Тип SELF ссылки
     */
    public abstract static class PredicateBuilder<MESSAGE,SCOPE,SELF extends PredicateBuilder<MESSAGE,SCOPE,SELF>> {
        /**
         * Строитель фильтров
         */
        public final Builder<MESSAGE,SCOPE> builder;

        /**
         * Конструктор
         * @param builder Строитель фильтров
         */
        public PredicateBuilder(Builder<MESSAGE,SCOPE> builder){
            this.builder = builder;
        }

        protected abstract void append(Function<SecurAccess<?,SCOPE>,Optional<MESSAGE>> filter);

        /**
         * Любая байт-инструкция
         * @param message сообщение
         * @return Строитель
         */
        public Builder<MESSAGE,SCOPE> any(MESSAGE message) {
            append( t -> Optional.of(message) );
            return builder;
        }

        /**
         * Проверка вызова метода
         * @param filter фильтр вызова метода
         * @param message сообщение
         * @return Строитель
         */
        public Builder<MESSAGE,SCOPE> invokeMethod(Predicate<InvokeMethod> filter, MESSAGE message) {
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof InvokeMethod ){
                    if( filter.test((InvokeMethod) ev) ){
                        return Optional.of(message);
                    };
                }
                return Optional.empty();
            });
            return builder;
        }

        /**
         * Проверка вызова BootStrap метода
         * @param filter фильтр вызова BootStrap метода
         * @param message сообщение
         * @return Строитель
         */
        public Builder<MESSAGE,SCOPE> invokeIndy(Predicate<InvokeDynamicCall> filter, MESSAGE message) {
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof InvokeDynamicCall ){
                    return filter.test((InvokeDynamicCall) ev) ? Optional.of(message) : Optional.empty();
                }
                return Optional.empty();
            });
            return builder;
        }

        /**
         * Проверка вызова метода/конструктора/BootStrap
         * @param message сообщение
         * @param filter фильтр вызова метода
         * @return Строитель
         */
        public Builder<MESSAGE,SCOPE> invoke(MESSAGE message, Predicate<Invoke<?>> filter) {
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof Invoke ){
                    return filter.test((Invoke<?>) ev) ? Optional.of(message) : Optional.empty();
                }
                return Optional.empty();
            });
            return builder;
        }

        /**
         * Проверка работы с полем (read/write)
         * @param message сообщение
         * @param filter фильтр инструкции
         * @return Строитель
         */
        public Builder<MESSAGE,SCOPE> field(MESSAGE message, Predicate<FieldAccess> filter){
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof FieldAccess ){
                    return filter.test((FieldAccess) ev) ? Optional.of(message) : Optional.empty();
                }
                return Optional.empty();
            });
            return builder;
        }
    }

    /**
     * Строитель разрешающего фильтра
     * @param <MESSAGE> сообщение
     * @param <SCOPE> область инструкций
     */
    public static class AllowBuilder<MESSAGE,SCOPE> extends PredicateBuilder<MESSAGE,SCOPE,AllowBuilder<MESSAGE,SCOPE>> {
        public AllowBuilder(Builder<MESSAGE,SCOPE> builder){
            super(builder);
        }

        @Override
        protected void append(Function<SecurAccess<?,SCOPE>,Optional<MESSAGE>> filter){
            builder.validators.add( ev -> {
                var res = filter.apply(ev);
                return res.map(message -> Tuple2.of(message, true));
            });
        }
    }

    /**
     * Строитель запрещающего фильтра
     * @param <MESSAGE> сообщение
     * @param <SCOPE> область инструкций
     */
    public static class DenyBuilder<MESSAGE,SCOPE> extends PredicateBuilder<MESSAGE,SCOPE, DenyBuilder<MESSAGE,SCOPE>> {
        public DenyBuilder(Builder<MESSAGE,SCOPE> builder){
            super(builder);
        }

        @Override
        protected void append(Function<SecurAccess<?,SCOPE>,Optional<MESSAGE>> filter){
            builder.validators.add( ev -> {
                var res = filter.apply(ev);
                return res.map(message -> Tuple2.of(message, false));
            });
        }
    }
}
