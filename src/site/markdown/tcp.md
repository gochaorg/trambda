TCP клиент / сервер
==========================

```xml
<dependency>
    <groupId>xyz.cofe</groupId>
    <artifactId>trambda-tcp</artifactId>
    <!-- Актуальную версию лучше поискать 
           на https://oss.sonatype.org/
           на https://search.maven.org/ 
    -->
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Будет рассмотрено

- Публикуем свой API
- Написание TCP сервера
- Написание TCP клиента
- Писать свой TCP сервер необязательно

Публикуем свой API
-------------------------

Допустим у нас есть библиотека (или API / набор интерфейсов), 
которая протестирована и работает локально.

Вот такая [простая библиотека](https://github.com/gochaorg/trambda/tree/381f59f0a41ceb83f9775e3fcfeca8028d88de00/trambda-tcp/src/test/java/xyz/cofe/trambda/tcp/demo):

```java
package xyz.cofe.trambda.tcp.demo;

/** Информация о процессе ОС */
public class OsProc implements Serializable {
    // Идентификатор родительского процесса
    private final Integer ppid;
    public Optional<Integer> getPpid(){ ... }
    
    // Идентификатор процесса
    private final Integer pid;
    public int getPid(){ return pid; }
    
    // Имя процесса
    private final String name;
    public String getName(){ return name; }
    
    // Командная строка процесса
    private final String cmdLine;
    public Optional<String> getCmdline(){ ... }
    ...
}

/** Интерфейс получения списка процессов */
public interface IEnv {
    // получения списка процессов
    public List<OsProc> processes();
}

/** Реализация интерфейса для Linux */
public class LinuxEnv implements IEnv { ... }

/** Реализация интерфейса для Windows */
public class WindowsEnv implements IEnv { ... }
```

Теперь что эта библиотека была клиент-серверной делаем сделаем следующие:

- Напишем сервер
- Напишем клиента

Написание сервера
--------------------------

```java
// Создание TCP сокета
ServerSocket ssocket = new ServerSocket(port);

// Настраиваем сокет
ssocket.setSoTimeout(1000*5);

// Создаем сервер
server = new TcpServer<IEnv>(
    // Передаем сокет
    ssocket,
    
    // Передаем функцию получения сервиса для новой сессии
    s -> new LinuxEnv(),
    
    // Настраиваем безопасность
    SecurityFilters.create(s -> {
        
        // Разрешаем вызовы строго - определенных методов 
        s.allow( a -> {
            
            // Публикуемый API нашего сервиса 
            a.invoke("demo api", c->
                c.getOwner().matches(
                    "xyz\\.cofe\\.trambda\\.tcp\\.demo\\.([\\w\\d]+)"));
            
            // Работа с коллекциями
            a.invoke("java collections api", c->c.getOwner().matches(
                "java\\.util\\.(List)|java\\.util\\.stream\\.([\\w\\d]+)"));
            
            // Работа с Java строками
            a.invoke("java lang api", c->
                c.getOwner().matches("java\\.lang\\.(String)"));
            
            // Методы которые использует компилятор Java
            a.invoke("java compiler", c->
                c.getOwner().matches(
                    "java\\.lang\\.invoke\\.(LambdaMetafactory|StringConcatFactory)"));
        });
        
        // Все остальное запрещаем
        s.deny().any("by default");
    })
);

// Указываем что Thread сервера будет запущен как фоновый
server.setDaemon(true);

// Запускаем сервер
server.start();
```

Написание клиента
--------------------------

Написание клиента еще проще:

```java
var tcpQuery = TcpQuery
    // Какой API мы будем использовать
    .create(IEnv.class)
    // Указываем адрес сервера
    .host("localhost").port(port)
    // Получаем Query<IEnv>
    .build();

// Выполняем запрос к серверу, получим список процессов chrome/java на сервере
var qRegex = "chrome|java";
tcpQuery.apply( env ->
    env.processes().stream()
    // Фильтруем список процессов
    .filter( p -> p.getName().matches("(?is).*("+qRegex+").*") )
    // Преобразуем Stream<OsProc> в List<OsProc>
    // т.к. List<?> - реализует Serializable
    // а Stream<?> - не реализует Serializable
    .collect(Collectors.toList())
)
    // Отображаем полученный список процессов
    .stream().map(OsProc::toString).forEach(log::info);
```

Писать свой TCP сервер необязательно
----------------------------------------

Вообще писать свой сервер необязательно, достаточно 

1. Взять пакет [trambda-tcp-serv-cli](https://github.com/gochaorg/trambda/tree/381f59f0a41ceb83f9775e3fcfeca8028d88de00/trambda-tcp-serv-cli)
   - Пакет имеет следующую структуру каталогов/файлов
        - `bin/` - каталог shell скриптами, для запуска сервера
           - `bin/trambda-tcp-serv` - shell скрипт для запуска сервера
           - `bin/trambda-tcp-serv.bat` - batch скрипт для запуска сервера
        - `jars/` - Каталог с библиотеками сервера
2. Скопировать свою библиотеку (свой API + реализацию его) в каталог `jars/`
3. Подготовить скрипт запуска, например `my_service.groovy`, его код, будет ниже
4. Запустить сервер с указанием скрипта `$ bin/trambda-tcp-serv -s my_service.groovy`

### my_service.groovy

```groovy
// Публикуем наш API, как некий сетевой сервис
app.service(
    // Адрес:порт - адрес на котором будет запущен сервис
    // адрес может быть например localhost:7890
    //   тогда сервис будет доступен только локально
    // Можно указывать не ip, а dns имя, например i-env.myserver.com:12345
    "0.0.0.0:9988", 
    new xyz.cofe.trambda.demo.api.LinuxEnv() 
) {
    daemon false
    // Указываем настройки безопасности
    security {
        // Какие методы будут доступныме
        allow {
            // Методы которые использует компилятор Java
            invoke( 'Java compiler' ){
                methodOwner ==~ /java\.lang\.invoke\.(LambdaMetafactory|StringConcatFactory)/
            }
            // Работа с коллекциями
            invoke( 'Java collections' ){
                methodOwner ==~ /java\.util\.(stream\.(Stream|Collectors)|(List))/
            }
            // Работа с Java строками
            invoke( 'Java lang' ){
                methodOwner ==~ /java\.lang\.String/
            }
            // Публикуемый API нашего сервиса 
            invoke( 'Api '){
                methodOwner ==~ /xyz\.cofe\.trambda\.demo\.api\.(IEnv|OsProc)/
            }
        }
        // Все остальные методы не будут доступны
        deny {
            any("ban all")
        }
    }
}

// Запускаем все наши сервисы
start();
```