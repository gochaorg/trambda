package xyz.cofe.trambda.tcp;

import java.io.ByteArrayInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.log.api.Logger;

/**
 * Сырой пакет доступный только для чтения
 */
public class RawPackReadonly extends RawPack {
    private static final Logger log = Logger.of(TcpSession.class);

    public RawPackReadonly(TcpHeader header, byte[] payload){
        super(header, payload);
    }
    public RawPackReadonly(RawPack sample){
        super(sample);
    }

    //region header
    @Override
    public TcpHeader getHeader(){
        return header;
    }

    @Override
    public void setHeader(TcpHeader header){
        throw new UnsupportedOperationException("readonly");
    }
    //endregion
    //region payload
    @Override
    public byte[] getPayload(){
        return payload;
    }

    @Override
    public void setPayload(byte[] payload){
        throw new UnsupportedOperationException("readonly");
    }
    //endregion

    private volatile boolean hashComputed = false;
    private volatile boolean hashMatched = false;

    /**
     * Проверяет что контрольная сумма указанная в заголовке совпадает с хешем полезной нагрузкой
     * @return true - совпадает
     */
    public boolean isPayloadChecksumMatched(){
        if( hashComputed )return hashMatched;
        synchronized( this ){
            if( hashComputed )return hashMatched;
            hashMatched = header.matched(payload,0,payload.length);
            hashComputed = true;
            return hashMatched;
        }
    }

    private volatile Message message;
    private volatile boolean messageComputed = false;

    /**
     * Возвращает сообщение из пакета
     * @return сообщение
     */
    public Message payloadMessage() {
        if( messageComputed )return message;
        synchronized( this ){
            if( messageComputed )return message;

            if( payload == null || payload.length < 1 ){
                message = null;
                messageComputed = true;
                return message;
            }

            message = Serializer.fromBytes(payload);
            messageComputed = true;
            return message;
        }
    }
}
