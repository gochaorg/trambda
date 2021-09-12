package xyz.cofe.trambda.tcp;

import java.util.Set;

/**
 * Издатель событий {@link TrEvent}
 */
public interface TrEventPublisher {
    /**
     * Добавление подписчика.
     * @param listener Подписчик.
     * @return Интерфейс для отсоединения подписчика
     */
    AutoCloseable addListener(TrListener listener);

    /**
     * Добавление подписчика.
     * @param listener Подписчик.
     * @param weakLink true - добавить как weak ссылку / false - как hard ссылку
     * @return Интерфейс для отсоединения подписчика
     */
    AutoCloseable addListener(TrListener listener, boolean weakLink);

    /**
     * Удаление всех подписчиков
     */
    void removeAllListeners();

    /**
     * Получение списка подписчиков
     * @return подписчики
     */
    Set<TrListener> getListeners();

    /**
     * Проверка наличия подписчика в списке обработки
     * @param listener подписчик
     * @return true - есть в списке обработки
     */
    boolean hasListener(TrListener listener);
}
