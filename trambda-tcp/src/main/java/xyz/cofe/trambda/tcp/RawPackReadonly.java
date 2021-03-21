package xyz.cofe.trambda.tcp;

import java.io.ByteArrayInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.cofe.text.Text;

public class RawPackReadonly extends RawPack {
    private static final Logger log = LoggerFactory.getLogger(TcpSession.class);

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
    public Message payloadMessage() {
        if( messageComputed )return message;
        synchronized( this ){
            if( messageComputed )return message;

            if( payload == null || payload.length < 1 ){
                message = null;
                messageComputed = true;
                return message;
            }

            message = Message.deserialize(payload);
            messageComputed = true;
            return message;
        }
    }
}
