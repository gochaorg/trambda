package xyz.cofe.bc.xml;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import xyz.cofe.text.IndentPrintWriter;
import xyz.cofe.text.IndentWriter;

public class XmlStream implements Closeable {
    private Writer out;

    public XmlStream(Writer writer){
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        out = writer;
    }

    private void htmlEncode( String text, boolean attr ) throws IOException{
        for( int i=0; i<text.length(); i++ ){
            char c0 = text.charAt(i);
            if( c0<32 ){
                out.write("&#x");
                out.write(StringCodec.hexOf(c0));
                out.write(";");
            }else if( c0=='<' ){
                out.write("&lt;");
            }else if( c0=='>' ){
                out.write("&gt;");
            }else if( c0=='&' ){
                out.write("&amp;");
            }else if( c0=='\'' && attr ){
                out.write("&apos;");
            }else if( c0=='\"' && attr ){
                out.write("&quot;");
            }else if( Character.isLetterOrDigit(c0) || c0<256 ){
                out.write(c0);
            }else {
                out.write("&#x");
                out.write(StringCodec.hexOf(c0));
                out.write(";");
            }
        }
    }

    private boolean openTag = false;
    private List<String> tagPath = new ArrayList<>();
    private List<Boolean> hasContent = new ArrayList<>();
    public synchronized boolean isOpenTag(){ return openTag; }

    public synchronized void start( String tag ){
        if( tag==null )throw new IllegalArgumentException( "tag==null" );
        tagPath.add(tag);
        hasContent.add(false);
        try{
            if( openTag ){
                out.write(">\n");
                openTag = false;
            }

            if( tagPath.size()>1 ){
                for( int i=1; i<tagPath.size(); i++ ){
                    out.write("  ");
                }
            }

            out.write("<");
            out.write(tag);
        } catch( IOException e ) {
            throw new IOError(e);
        }
        openTag = true;
    }
    public synchronized void attribute( String name, String value ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( value==null )throw new IllegalArgumentException( "value==null" );
        try{
            if( openTag ){
                out.write(" ");
                htmlEncode(name,true);
                out.write("=\"");
                htmlEncode(value,true);
                out.write("\"");
            }
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }
    public synchronized void text( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        try{
            if( openTag ){
                out.write(">");
                openTag = false;
            }
            if( !hasContent.isEmpty() ){
                hasContent.set(hasContent.size()-1,true);
            }

            htmlEncode(text,false);
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }
    public synchronized void end(){
        try {
            if( !tagPath.isEmpty() ){
                var tag = tagPath.remove(tagPath.size()-1);
                var hasContent = this.hasContent.remove(this.hasContent.size()-1);
                if( openTag ){
                    out.write("/>\n");
                    openTag = false;
                }else {
                    if( !hasContent && tagPath.size()>0 ){
                        for( int i=0; i<tagPath.size(); i++ ){
                            out.write("  ");
                        }
                    }
                    out.write("</");
                    out.write(tag);
                    out.write(">\n");
                }
            }
            out.flush();
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    public synchronized void comment( String comment ){
        try {
            if( comment==null )throw new IllegalArgumentException( "comment==null" );

            if( openTag ){
                out.write(">");
                openTag = false;
            }

            comment = comment.replace("<!--", "&lt;--");
            comment = comment.replace("-->", "--&gt;");
            out.write("<!--");
            out.write(comment);
            out.write("-->");
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    public synchronized void flush(){
        try{
            out.flush();
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        out.flush();
    }
}
