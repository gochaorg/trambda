Ядро
=================

Основное ядро, модуль - `trambda-core`.

```xml
<dependency>
    <groupId>xyz.cofe</groupId>
    <artifactId>trambda-core</artifactId>
    <!-- Актуальную версию лучше поискать 
           на https://oss.sonatype.org/
           на https://search.maven.org/ 
    -->
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Задачи модуля

- Получить байт-код лямбды переданной в качестве параметра
- (Де)Сериализация байт-кода
- Генерация класса JVM для сериализованного представления
- Функции проверки безопасности байт-кода

Получение байт-кода и его сериализация
----------------------------------------

Данной функций занимается класс `xyz.cofe.trambda.AsmQuery`.

Данный класс реализует интерфейс `xyz.cofe.trambda.Query<ENV>`:

```java
/**
 * Общий интерфейс для вызова лямбды на сервере
 * @param <ENV> Сервис предоставляемый на сервере
 */
public interface Query<ENV> {
    /**
     * Вызов лямбды на сервере
     * @param fn лямбда
     * @param <RES> Сервис предоставляемый на сервере
     * @return результат вычисления на сервере
     */
    public <RES> RES apply( Fn<ENV,RES> fn );
}
```

`Fn` - Это функция от одного параметра:

```java
package xyz.cofe.trambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Лямбда передаваемая на сервер
 * @param <A> Тип сервиса который доступен ра сервере
 * @param <Z> Тип результата возвращаемый с сервера
 */
public interface Fn<A,Z> extends Serializable, Function<A,Z> {
    /**
     * Вызов лямбды
     * @param a сервис передаваемый в лямбду
     * @return результата возвращаемый с сервера
     */
    public Z apply(A a);
}
```

Для того что бы получить байт-код лямбды, 
необходимо создать потомка от `AsmQuery`

```java
public class AsmQuery<ENV> implements Query<ENV> {
    ...
    /**
     * Сериализация и вызов лямбды
     * @param fn лямбда
     * @param <RES> результат вызова
     * @return результат вызова
     */
    @Override
    public <RES> RES apply(Fn<ENV, RES> fn){
        ...
    }
    ...
    /**
     * Реализация вызова лямбды
     * @param fn лямбда
     * @param sl лямбда - сериализация
     * @param mdef байт-код лямбды
     * @param <RES> результат вызова
     * @return результат вызова
     */
    protected <RES> RES call( Fn<ENV, RES> fn, SerializedLambda sl, LambdaDump mdef ){
        return null;
    }
    ...
}
```

Это клас по сути является абстрактным, и для реализации конечной функциональности 
требуется переопределить метод `call(Fn, SerializedLambda, LambdaDump)`

Данный класс по сути выполняет следующие функции:

- Метода `apply(Fn)` - получает байт код fn
- Полученный байт код передает в `call(Fn, SerializedLambda, LambdaDump)`

```java
AtomicReference<LambdaDump> mdefRef = new AtomicReference<>();
var res = new AsmQuery<IEnv>(){
    @Override
    protected   RES call(Fn<IEnv, RES> fn, SerializedLambda sl, LambdaDump mdef){
        // Сохранение представления байт кода
        mdefRef.set(mdef);
        // ...
        return netClient.call(fn, sl, mdef);
    }
}.apply( env0 -> 
    env0.getUsers().filter(
        u -> u.getName().contains("Petrov")
    )
);
```

### LambdaDump

`xyz.cofe.trambda.LambdaDump` - Это сериализованное представление байт-кода.


Генерация класса JVM для сериализованного представления
----------------------------------------------------------

Для восстановления байт-кода из сериализованного представления используется
класс `xyz.cofe.trambda.LambdaDump`

Данный класс умеет восстанавливать как байт код, 
так и возвращает ссылку на целевой метод ([Method](https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Method.html)) 

```java
Method method = dump.restore().method();
```

Функции проверки безопасности байт-кода
----------------------------------------

Реализована следующая проверка

- Проверка вызова метода
- Проверка чтения/записи в поля класса (field)

```java
import xyz.cofe.trambda.sec.SecurityFilters;
import xyz.cofe.trambda.sec.SecurityFilter;
import xyz.cofe.trambda.sec.SecurAccess;

var secAcc = SecurAccess.inspect(dump);

var sfilters = SecurityFilters.create()
    // Разрешаем вызовы
    .allow(a -> {
        // Java компилятор генерирует в байт коде вызовы
        // методов указанных классов
        a.invoke("Java compiler", call -> 
            call.getOwner().equals("java.lang.invoke.StringConcatFactory"));
        a.invoke("Java compiler", call -> 
            call.getOwner().equals("java.lang.invoke.LambdaMetafactory"));
    })
    // Разрешаем вызовы
    .allow(a -> {
        // Доступ к stdio, для чтения
        a.field("System stdio", f -> 
            f.getOwner().equals("java.lang.System") && f.isReadAccess());
        // Доступ к java stream, java collection
        a.invoke("Java Streams", 
            f -> f.getOwner().matches("java\\.io\\.[\\w\\d]*(Stream|Writer)[\\w\\d]*"));
        // Доступ к методам класса java.lang.String
        a.invoke("api Java lang", c -> 
            c.getOwner().matches("java.lang.String"));
    })
    // Запрещаем вызовы
    .deny(b -> {
        // Запрещаем изменять поля out, err, in класса System
        b.field("deny System field write", 
            f -> f.getOwner().equals("java.lang.System") && f.isWriteAccess());
        
        // Запрещаем вызывать чувствительные методы класса System
        b.invoke("deny call System method", 
            f -> f.getOwner().equals("java.lang.System") && f.getMethodName().matches(
            "(?i)gc|exit|console|clear.*|" +
            "getSecurity.*|inherited.*|load.*|map.*|run.*|set.*|wait.*"));
    })
    // Разрешаем вызовы нашего сервиса
    .allow( a -> {
        a.invoke("api by xyz.cofe", c -> c.getOwner().matches("xyz.cofe.iter.[\\w\\d]+"));
        a.invoke("api by xyz.cofe", c -> c.getOwner().matches("xyz.cofe.[\\w\\d]+"));
        a.invoke("api by trambda", c -> c.getOwner().matches("xyz.cofe.trambda.[\\w\\d]+"));
    })
    // Запрещаем все остальные вызовы
    .deny().any("Deny by default")
    .build();

// Выполняем проверку байт-кода
sfilters.validate(secAcc).forEach(sm -> System.out.println(
    "allow="+sm.isAllow()+
    " message="+sm.getMessage()+
    " access="+sm.getAccess()
    ));
```