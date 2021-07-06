package xyz.cofe.bc.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ReadContext {
    public ReadContext(){
    }

    public ReadContext(ReadContext sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        if( sample.xmlPath!=null ) xmlPath = new ArrayList<>(sample.xmlPath);
        if( sample.objectPath!=null ) objectPath = new ArrayList<>(sample.objectPath);
        if( sample.textPath!=null ) textPath = new ArrayList<>(sample.textPath);
        if( sample.commentPath!=null ) commentPath = new ArrayList<>(sample.commentPath);
        if( sample.xmlEvent!=null ) xmlEvent = sample.xmlEvent;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ReadContext clone(){
        return new ReadContext(this);
    }

    public ReadContext head( int count ){
        ReadContext ctx = clone();
        if( count<1 ){
            ctx.xmlEvent = null;
            if( ctx.xmlPath!=null )ctx.xmlPath.clear();
            if( ctx.objectPath!=null )ctx.objectPath.clear();
            if( ctx.textPath!=null )ctx.textPath.clear();
            if( ctx.commentPath!=null ) ctx.commentPath.clear();
        }else {
            if( ctx.xmlPath!=null ){
                int dropCnt = ctx.xmlPath.size() - count;
                if( dropCnt > 0 ){
                    for( int i = 0; i < dropCnt; i++ ){
                        ctx.xmlPath.remove(ctx.xmlPath.size() - 1);
                        if( ctx.objectPath!=null )ctx.objectPath.remove(ctx.objectPath.size() - 1);
                        if( ctx.textPath!=null )ctx.textPath.remove(ctx.textPath.size() - 1);
                        if( ctx.commentPath!=null )ctx.commentPath.remove(ctx.commentPath.size() - 1);
                    }
                    if( !ctx.xmlPath.isEmpty() ){
                        ctx.xmlEvent = ctx.xmlPath.get(ctx.xmlPath.size() - 1);
                    }
                }
            }
        }
        return ctx;
    }
    public ReadContext dropLast( int cnt ){
        if( cnt<=0 ){
            return clone();
        }else {
            if( xmlPath!=null ){
                return head(xmlPath.size() - cnt);
            }
            return clone();
        }
    }
    public ReadContext dropLast(){
        return dropLast(1);
    }
    public boolean isEmpty(){ return xmlPath==null || xmlPath.isEmpty(); }

    //region xmlPath : List<XMLEvent>
    protected List<XMLEvent> xmlPath;
    public List<XMLEvent> getXmlPath(){ return xmlPath; }
    public void setXmlPath(List<XMLEvent> xmlPath){ this.xmlPath = xmlPath; }
    //endregion
    //region objectPath : List<Object>
    protected List<Object> objectPath;
    public List<Object> getObjectPath(){ return objectPath; }
    public void setObjectPath(List<Object> objectPath){ this.objectPath = objectPath; }
    //endregion
    //region xmlEvent : XMLEvent
    protected XMLEvent xmlEvent;
    public XMLEvent getXmlEvent(){ return xmlEvent; }
    public void setXmlEvent(XMLEvent xmlEvent){ this.xmlEvent = xmlEvent; }
    //endregion

    //region textPath : List<StringBuilder>
    protected List<StringBuilder> textPath;
    public List<StringBuilder> getTextPath(){ return textPath; }
    public void setTextPath(List<StringBuilder> lst){ textPath=lst; }
    //endregion
    //region commentPath : List<StringBuilder>
    protected List<StringBuilder> commentPath;
    public List<StringBuilder> getCommentPath(){ return commentPath; }
    public void setCommentPath(List<StringBuilder> lst){ commentPath=lst; }
    //endregion

    //region construct(obj)
    public void contribute(Object obj){
        if( obj==null )throw new IllegalArgumentException( "obj==null" );
        if( !objectPath.isEmpty() ){
            objectPath.set(objectPath.size()-1, obj);
        }
    }
    //endregion
    //region find(Class<A> cls):Optional<A>
    public <A> Optional<A> find(Class<A> cls){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        var objPath = objectPath;
        if( objPath!=null ){
            for( int i=objPath.size()-1; i>=0; i-- ){
                var obj = objPath.get(i);
                if( obj!=null && cls.isAssignableFrom(obj.getClass()) ){
                    //noinspection unchecked
                    return Optional.of( (A)obj );
                }
            }
        }
        return Optional.empty();
    }
    //endregion

    public boolean isStartElement(){
        return xmlEvent instanceof StartElement;
    }
    public boolean isEndElement(){
        return xmlEvent instanceof EndElement;
    }
    public String text(boolean hexDecode){
        String txt = null;

        //txt = textContent!=null ? textContent.toString() : "";
        if( textPath!=null && !textPath.isEmpty() ){
            txt = textPath.get(textPath.size()-1).toString();
        }else{
            txt = "";
        }

        if( hexDecode && !xmlPath.isEmpty() ){
            StartElement el = null;
            for( var i=xmlPath.size()-1; i>=0; i-- ){
                var e = xmlPath.get(i);
                if( e instanceof StartElement ){
                    el = (StartElement)e;
                    break;
                }
            }
            if( el!=null ){
                var iattr = el.getAttributes();
                while( iattr.hasNext() ){
                    var attr = iattr.next();
                    if( attr.getName().getLocalPart().equals("encode") ){
                        var aval = attr.getValue();
                        if( aval!=null && aval.equalsIgnoreCase("hex") ){
                            Object otxt = HexCodec.deserialize(txt);
                            if( otxt instanceof String ){
                                return otxt.toString();
                            }
                        }
                    }
                }
            }
        }
        return txt;
    }
    public String text(){
        return text(true);
    }

    private static Pattern intPattern = Pattern.compile("[+\\-]?\\d+");
    private static Pattern floatPattern = Pattern.compile("[+\\-]?(\\d+)(\\.\\d+)?");
    private static Pattern booleanPattern = Pattern.compile("(?is)true|false");

    public Optional<StartElement> element(){
        if( xmlEvent instanceof StartElement )return Optional.of((StartElement) xmlEvent);
        if( xmlPath!=null && !xmlPath.isEmpty() ){
            for( int i=xmlPath.size()-1; i>=0; i++ ){
                var el = xmlPath.get(i);
                if( el instanceof StartElement ){
                    return Optional.of((StartElement) el);
                }
            }
        }
        return Optional.empty();
    }

    //region attribute
    public class Attr {
        protected Predicate<QName> attributeName;
        public void attribute( String name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            attributeName = qname -> qname.getLocalPart().equals(name);
        }
        public void attribute( Predicate<QName> name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            attributeName = name;
        }

        public <A> Optional<A> tryRead(Predicate<String> filter, Function<String,A> translate){
            var el = element();
            if( el.isPresent() && attributeName!=null ){
                var iattr = el.get().asStartElement().getAttributes();
                while( iattr.hasNext() ){
                    var attr = iattr.next();
                    var value = attr.getValue();
                    if( attributeName.test(attr.getName()) && value!=null && filter.test(value) ){
                        return Optional.of(translate.apply(value));
                    }
                }
            }
            return Optional.empty();
        }

        public Optional<String> tryString(){
            return tryRead( x -> true, x -> x );
        }

        public Optional<Byte> tryByte(){
            return tryRead( s -> intPattern.matcher(s).matches(), Byte::parseByte );
        }
        public Optional<Short> tryShort(){
            return tryRead( s -> intPattern.matcher(s).matches(), Short::parseShort );
        }
        public Optional<Integer> tryInteger(){
            return tryRead( s -> intPattern.matcher(s).matches(), Integer::parseInt );
        }
        public Optional<Long> tryLong(){
            return tryRead( s -> intPattern.matcher(s).matches(), Long::parseLong );
        }
        public Optional<Float> tryFloat(){
            return tryRead( s -> floatPattern.matcher(s).matches(), Float::parseFloat );
        }
        public Optional<Double> tryDouble(){
            return tryRead( s -> floatPattern.matcher(s).matches(), Double::parseDouble );
        }
        public Optional<Boolean> tryBoolean(){
            return tryRead( s -> booleanPattern.matcher(s).matches(), Boolean::parseBoolean );
        }
    }
    protected Attr attr = new Attr();
    public Attr attribute( String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        attr.attribute(name);
        return attr;
    }
    public class AttrNameFilter {
        public Attr eq( String name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            attr.attribute(name);
            return attr;
        }
        public Attr regex(String pattern){
            if( pattern==null )throw new IllegalArgumentException( "pattern==null" );
            Pattern ptn = Pattern.compile(pattern);
            attr.attribute( qName -> ptn.matcher(qName.getLocalPart()).find() );
            return attr;
        }
    }
    protected AttrNameFilter attrFilter = new AttrNameFilter();
    public AttrNameFilter attr(){
        return attrFilter;
    }
    //endregion
}
