package xyz.cofe.trambda.tcp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BuffInputStream extends BufferedInputStream {
    public BuffInputStream(InputStream in){
        super(in);
    }

    public BuffInputStream(InputStream in, int size){
        super(in, size);
    }

    public int pos(){ return pos; }
    public int markpos(){ return markpos; }
    public int count(){ return count; }
}
