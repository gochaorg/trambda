package xyz.cofe.bc.xml;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Predicate;
import xyz.cofe.trambda.bc.cls.CBegin;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class BCDeserializer {
    public BCDeserializer(){
    }

    private volatile Map<String,TagReader> readers;
    private volatile Map<Predicate<ReadContext>,TagReader> restores;

    public BCDeserializer readers( Map<String,TagReader> readers ){
        this.readers = readers;
        return this;
    }

    public synchronized BCDeserializer registry( String tag, TagReader reader ){
        if( tag==null )throw new IllegalArgumentException( "tag==null" );
        if( reader==null )throw new IllegalArgumentException( "reader==null" );
        var rdrs = readers;
        if( rdrs==null ){
            readers = new HashMap<>();
            rdrs = readers;
        }
        rdrs.put(tag, reader);
        return this;
    }
    public synchronized BCDeserializer registry( Predicate<ReadContext> condition, TagReader reader ){
        if( condition==null )throw new IllegalArgumentException( "condition==null" );
        if( reader==null )throw new IllegalArgumentException( "reader==null" );
        if( restores==null ){
            restores = new LinkedHashMap<>();
        }
        restores.put(condition, reader);
        return this;
    }

    public static XMLEventReader xmlEventReader(Reader reader){
        if( reader==null )throw new IllegalArgumentException( "reader==null" );
        try{
            return XMLInputFactory.newDefaultFactory().createXMLEventReader(reader);
        } catch( XMLStreamException e ) {
            throw new IOError(e);
        }
    }
    public static XMLEventReader xmlEventReader(String xml){
        if( xml==null )throw new IllegalArgumentException( "xml==null" );
        try{
            return XMLInputFactory.newDefaultFactory().createXMLEventReader(new StringReader(xml));
        } catch( XMLStreamException e ) {
            throw new IOError(e);
        }
    }
    public static XMLEventReader xmlEventReader(InputStream stream, String charset){
        if( stream==null )throw new IllegalArgumentException( "stream==null" );
        if( charset==null )throw new IllegalArgumentException( "charset==null" );
        try{
            return xmlEventReader(new InputStreamReader(stream,charset));
        } catch( UnsupportedEncodingException e ) {
            throw new IOError(e);
        }
    }
    public static XMLEventReader xmlEventReader(URL stream, String charset){
        if( stream==null )throw new IllegalArgumentException( "stream==null" );
        if( charset==null )throw new IllegalArgumentException( "charset==null" );
        try( var s = stream.openStream() ){
            return xmlEventReader(new InputStreamReader(s,charset));
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    protected Map<String,TagReader> readers(){
        if( readers!=null )return readers;
        synchronized( this ){
            if( readers!=null )return readers;
            readers = new HashMap<>();
            for( var reg : ServiceLoader.load(DeserializeRegistry.class) ){
                reg.registry(this);
            }
            return readers;
        }
    }

    protected List<XMLEvent> xmlPath;
    protected List<Object> objectPath;
    protected List<StringBuilder> textPath;
    protected List<StringBuilder> commentPath;

    public Object restore(Reader xml){
        return restore(xmlEventReader(xml));
    }
    public Object restore(String xml){
        return restore(xmlEventReader(xml));
    }
    public Object restore(InputStream url, String charset){
        return restore(xmlEventReader(url, charset));
    }
    public Object restore(URL url, String charset){
        return restore(xmlEventReader(url, charset));
    }
    public Object restore(XMLEventReader xmlData){
        if( xmlData==null )throw new IllegalArgumentException( "xmlData==null" );
        xmlPath = new ArrayList<>();
        objectPath = new ArrayList<>();
        textPath = new ArrayList<>();
        commentPath = new ArrayList<>();

        StringBuilder textContent = new StringBuilder();
        StringBuilder commentContent = new StringBuilder();
        List<Object> lastCreated = new LinkedList<>();
        int keepLast = 10;

        ReadContext ctx = new ReadContext();
        ctx.setXmlPath(xmlPath);
        ctx.setObjectPath(objectPath);
        ctx.setTextPath(textPath);
        ctx.setCommentPath(commentPath);

        var readers = readers();

        Runnable run_restores = ()->{
            var restores = this.restores;
            if( restores!=null ){
                for( var e : restores.entrySet() ){
                    if( e==null )continue;
                    if( e.getKey()==null )continue;
                    if( e.getValue()==null )continue;
                    if( !e.getKey().test(ctx) )continue;
                    e.getValue().read(ctx);
                }
            }
        };

        while( xmlData.hasNext() ){
            try{
                var event = xmlData.nextEvent();
                ctx.setXmlEvent(event);

                if( event instanceof StartElement ){
                    xmlPath.add(event);
                    objectPath.add(null);
                    textPath.add(new StringBuilder());
                    commentPath.add(new StringBuilder());

                    textContent.setLength(0);
                    commentContent.setLength(0);

                    var el = (StartElement)event;
                    var tagName = el.getName().getLocalPart();
                    var reader = readers.get(tagName);
                    if( reader!=null ){
                        reader.read(ctx);
                    }

                    run_restores.run();
                }else if( event instanceof EndElement ){
                    var el = (EndElement)event;
                    var tagName = el.getName().getLocalPart();
                    var reader = readers.get(tagName);
                    if( reader!=null ){
                        reader.read(ctx);
                    }

                    run_restores.run();

                    if( !xmlPath.isEmpty() ){
                        xmlPath.remove(xmlPath.size()-1);
                    }
                    if( !objectPath.isEmpty() ){
                        var removed = objectPath.remove(objectPath.size()-1);
                        if( removed!=null ){
                            lastCreated.add(0,removed);
                            if( lastCreated.size()>keepLast && keepLast>0 ){
                                var dropCnt = lastCreated.size() - keepLast;
                                for( int i=0; i<dropCnt; i++ ){
                                    lastCreated.remove(lastCreated.size()-1);
                                }
                            }
                        }
                    }
                    if( !textPath.isEmpty() ){
                        textPath.remove(textPath.size()-1);
                    }
                    if( !commentPath.isEmpty() ){
                        commentPath.remove(commentPath.size()-1);
                    }
                }else if( event instanceof Characters ){
                    var chr = (Characters)event;
                    var txt = chr.getData();
                    if( txt!=null ){
                        textContent.append(txt);
                        if( textPath!=null ){
                            textPath.get(textPath.size()-1).append(txt);
                        }
                    }
                }else if( event instanceof Comment ){
                    var cmt = (Comment)event;
                    var txt = cmt.getText();
                    if( txt!=null ){
                        commentContent.append(txt);
                        if( commentPath!=null ){
                            commentPath.get(commentPath.size()-1).append(txt);
                        }
                    }
                }
            } catch( XMLStreamException e ) {
                throw new IOError(e);
            }
        }
        return !lastCreated.isEmpty() ? lastCreated.get(0) : null;
    }
}
