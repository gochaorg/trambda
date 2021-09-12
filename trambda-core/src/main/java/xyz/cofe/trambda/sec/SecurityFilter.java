package xyz.cofe.trambda.sec;

import java.util.List;

/**
 * Фильтр безопасности, для проверки байт-кода
 * @param <MESSAGE> Сообщение генерируемое фильтром
 * @param <SCOPE> Область проверяемых данных
 */
public interface SecurityFilter<MESSAGE,SCOPE> {
    /**
     * Проверка байт-кода
     * @param secur Проверяемые инструкции
     * @return Результат проверки
     */
    public List<SecurMessage<MESSAGE,SCOPE>> validate( List<SecurAccess<?,SCOPE>> secur );
}
