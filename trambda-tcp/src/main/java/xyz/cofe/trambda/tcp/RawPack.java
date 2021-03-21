package xyz.cofe.trambda.tcp;

import java.util.Arrays;
import java.util.Map;

public class RawPack {
    public void RawPack(){
        header = new TcpHeader(0,"!RawPack", Map.of());
        payload = empty;
    }
    public RawPack(TcpHeader header, byte[] payload){
        if( header==null )throw new IllegalArgumentException( "header==null" );
        if( payload==null )throw new IllegalArgumentException( "payload==null" );
        this.header = header;
        this.payload = payload;
    }
    public RawPack(RawPack sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.header = sample.header;
        this.payload = sample.payload!=null ? Arrays.copyOf(sample.payload,sample.payload.length) : empty;
    }

    private static final byte[] empty = new byte[0];

    //region header
    protected TcpHeader header;
    public TcpHeader getHeader(){
        synchronized( this ){
            return header;
        }
    }
    public void setHeader(TcpHeader header){
        synchronized( this ){
            if( header == null ) throw new IllegalArgumentException("header==null");
            this.header = header;
        }
    }
    //endregion
    //region payload
    protected byte[] payload;
    public byte[] getPayload(){
        synchronized( this ){
            return payload;
        }
    }
    public void setPayload(byte[] payload){
        if( payload==null )throw new IllegalArgumentException( "payload==null" );
        synchronized( this ){
            this.payload = payload;
        }
    }
    //endregion

    public RawPackReadonly toReadonly(){
        synchronized( this ){
            return new RawPackReadonly(this);
        }
    }
}
