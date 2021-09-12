package xyz.cofe.trambda.tcp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Буфферизированный поток, с информацией о текущем состоянии
 */
public class BuffInputStream extends BufferedInputStream {
    /**
     * Конструктор
     * @param in входящий поток
     */
    public BuffInputStream(InputStream in){
        super(in);
    }

    /**
     * Конструктор
     * @param in входящий поток
     * @param size размер буфера в байтах
     */
    public BuffInputStream(InputStream in, int size){
        super(in, size);
    }

    /**
     * The value of the pos field at the time the last mark method was called.
     * This value is always in the range -1 through pos.
     * If there is no marked position in the input stream,
     * this field is -1.
     *
     * If there is a marked position in the input stream,
     * then buf[markpos] is the first byte to be supplied as input after a reset operation.
     *
     * If markpos is not -1, then all bytes from positions buf[markpos] through buf[pos-1]
     * must remain in the buffer array (though they may be moved to another place in the buffer array,
     * with suitable adjustments to the values of count, pos, and markpos);
     *
     * they may not be discarded unless and until the difference between pos and markpos exceeds marklimit.
     * @return метка текущей позиции в буфере
     */
    public int pos(){ return pos; }

    /**
     * Отметка текущей позиции
     * @return Отметка текущей позиции
     */
    public int markpos(){ return markpos; }

    /**
     * Отметка доступных данных в буфере
     * @return Отметка доступных данных в буфере
     */
    public int count(){ return count; }
}
