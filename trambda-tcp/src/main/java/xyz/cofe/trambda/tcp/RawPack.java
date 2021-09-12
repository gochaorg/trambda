package xyz.cofe.trambda.tcp;

import java.util.Arrays;
import java.util.Map;

/**
 * "Сырой" входящий пакет, полезная нагрузка которого не десерилизована
 */
public class RawPack {
    /**
     * Конструктор
     */
    public void RawPack(){
        header = new TcpHeader(0,"!RawPack", Map.of());
        payload = empty;
    }

    /**
     * Конструктор
     * @param header заголовок пакета
     * @param payload полезная нагрузка
     */
    public RawPack(TcpHeader header, byte[] payload){
        if( header==null )throw new IllegalArgumentException( "header==null" );
        if( payload==null )throw new IllegalArgumentException( "payload==null" );
        this.header = header;
        this.payload = payload;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public RawPack(RawPack sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.header = sample.header;
        this.payload = sample.payload!=null ? Arrays.copyOf(sample.payload,sample.payload.length) : empty;
    }

    private static final byte[] empty = new byte[0];

    //region header

    /**
     * Заголовок пакета
     */
    protected TcpHeader header;

    /**
     * Возвращает заголовок пакета
     * @return заголовок
     */
    public TcpHeader getHeader(){
        synchronized( this ){
            return header;
        }
    }

    /**
     * Указывает заголовок пакета
     * @param header заголовок
     */
    public void setHeader(TcpHeader header){
        synchronized( this ){
            if( header == null ) throw new IllegalArgumentException("header==null");
            this.header = header;
        }
    }
    //endregion
    //region payload
    protected byte[] payload;

    /**
     * Возвращает полезную нагрузку
     * @return полезная нагрузка
     */
    public byte[] getPayload(){
        synchronized( this ){
            return payload;
        }
    }

    /**
     * Указывает полезную нагрузку
     * @param payload полезная нагрузка
     */
    public void setPayload(byte[] payload){
        if( payload==null )throw new IllegalArgumentException( "payload==null" );
        synchronized( this ){
            this.payload = payload;
        }
    }
    //endregion

    /**
     * Клонирует пакет и делает доступным только для чтения
     * @return клон
     */
    public RawPackReadonly toReadonly(){
        synchronized( this ){
            return new RawPackReadonly(this);
        }
    }
}
