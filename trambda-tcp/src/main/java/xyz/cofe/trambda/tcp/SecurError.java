package xyz.cofe.trambda.tcp;

import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.sec.SecurMessage;
import xyz.cofe.trambda.sec.SecurityFilters;

/**
 * Ошибка при проверки кода на безопасность.
 * <p>
 * Данная ощибка возникает 
 * при проверке сервером на безопасность клиентского выполнения кода.
 * Если сервер обнаруживает не безопасный код, 
 * то он не допускает его выполнение и возвращает эту ошибку клиенту.
 * На стороне клиента будет сгенерировано исключение с этой ошибокой.
 * 
 * <h2>Настройка безопасности сервера</h2>
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
 */
public class SecurError extends Error {
    /**
     * Конструктор
     * @param message Сообщение о ошибке
     */
    public SecurError(String message){
        super(message);
        securMessages = List.of();
    }

    /**
     * Конструктор
     * @param message сообщение о ошибке
     * @param cause Причина ошибки
     */
    public SecurError(String message, Throwable cause){
        super(message, cause);
        securMessages = List.of();
    }

    /**
     * Конструктор
     * @param cause Причина ошибки
     */
    public SecurError(Throwable cause){
        super(cause);
        securMessages = List.of();
    }

    protected SecurError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
        securMessages = List.of();
    }

    private static String extractMessage(List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> secMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Lambda contains denied byte code:").append("\n");
        if( secMessages!=null){
            for( var msg : secMessages ){
                if( msg==null || msg.isAllow() )continue;
                sb.append(msg.getMessage()).append(" for ").append(msg.getAccess()).append("\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * Конструктор
     * @param secMessages Список сообщений о нарушении безопасности согласно фильтрам байт кода
     */
    public SecurError(List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> secMessages){
        super(extractMessage(secMessages),null,false,false);
        securMessages = List.copyOf(secMessages);
    }

    private final List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> securMessages;
    
    /**
     * Возвращает список сообщений о нарушении безопасности согласно фильтрам байт кода
     * @return список сообщений о нарушении безопасности
     */
    public List<SecurMessage<String, Tuple2<LambdaDump, LambdaNode>>> getSecurMessages(){ return securMessages; }
}
