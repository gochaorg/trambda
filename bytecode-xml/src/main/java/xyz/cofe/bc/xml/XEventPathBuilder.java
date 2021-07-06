package xyz.cofe.bc.xml;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class XEventPathBuilder {
    public static class AttributeFilter {
        protected final Predicate<Attribute> filter;
        public AttributeFilter(Predicate<Attribute> filter){
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            this.filter = filter;
        }

        protected Optional<Attribute> first(StartElement el){
            if( el==null )throw new IllegalArgumentException( "el==null" );
            var itr = el.getAttributes();
            while( itr.hasNext() ){
                var atr = itr.next();
                if( filter.test(atr) )return Optional.of(atr);
            }
            return Optional.empty();
        }

        public Predicate<ReadContext> test( Predicate<String> value ){
            if( value==null )throw new IllegalArgumentException( "value==null" );
            return ev -> ev.element().flatMap(el -> first(el).map(a -> {
                var str = a.getValue();
                if( str==null )return false;
                return value.test(str);
            })).orElse(false);
        }

        public Predicate<ReadContext> eq( String value ){
            if( value==null )throw new IllegalArgumentException( "value==null" );
            return test(value::equals);
        }

        public Predicate<ReadContext> eq( int value ){
            return test(s -> s.equals(Integer.toString(value)) );
        }

        public Predicate<ReadContext> eq( long value ){
            return test(s -> s.equals(Long.toString(value)) );
        }

        public Predicate<ReadContext> eq( boolean value ){
            return test(s -> s.equals(Boolean.toString(value)) );
        }
    }
    public static AttributeFilter attribute( String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return new AttributeFilter( attr -> attr.getName().getLocalPart().equals(name) );
    }

    public static class TagFilter implements Predicate<ReadContext> {
        public final Predicate<ReadContext> filter;
        public TagFilter( Predicate<ReadContext> filter ){
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            this.filter = filter;
        }

        @Override
        public boolean test(ReadContext readContext){
            return filter.test(readContext);
        }

        protected int repeatMin;
        public int repeatMin(){ return repeatMin; }
        public TagFilter repeatMin(int min){
            repeatMin = min;
            if( repeatMax<repeatMin ){
                repeatMax = repeatMin;
            }
            return this;
        }

        protected int repeatMax;
        public int repeatMax(){ return repeatMax; }
        public TagFilter repeatMax(int max){
            repeatMax = max;
            return this;
        }

//        protected boolean greedily = false;
//        public boolean greedily(){ return greedily; }
//        public TagFilter greedily( boolean greedily ){
//            this.greedily = greedily;
//            return this;
//        }

        public boolean isRepeating(){ return repeatMax > 0;  }

//        public TagFilter repeat( int min, int max, boolean greedily ){
//            return repeatMax(max).repeatMin(min).greedily(greedily);
//        }

        public TagFilter repeat( int min, int max ){
            return repeatMax(max).repeatMin(min);
        }

        private String name;
        public TagFilter name(String name){
            this.name = name;
            return this;
        }

        @Override
        public String toString(){
            if( name!=null )return name;
            return super.toString();
        }
    }

    public static TagFilter tag( String name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return new TagFilter(
            ev -> ev.element().map(el -> el.getName().getLocalPart().equals(name) ).orElse( false )
        ).name( "tag("+name+")" );
    }
    public static TagFilter tagName( Predicate<String> name ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        return new TagFilter(ev -> {
            return ev.element().map( el -> name.test(el.getName().getLocalPart()) ).orElse( false );
        });
    }
    public static TagFilter anyTag(){
        return tagName( x -> true ).name("anyTag");
    }

    @SafeVarargs
    public static Predicate<ReadContext> tail(Predicate<ReadContext> ... tagz ){
        if( tagz==null )throw new IllegalArgumentException( "tagz==null" );
        return event -> {
            var ev = event;
            var filterIndex = tagz.length;
            var result = true;
            var matchAll = false;
            while( filterIndex>0 ){
                filterIndex--;

                var filter = tagz[filterIndex];
                if( filter==null )continue;

                if( (filter instanceof TagFilter) && (((TagFilter)filter).isRepeating()) ){
                    var tfilter = (TagFilter)filter;
                    var nextFilter = filterIndex>0 ? tagz[filterIndex-1] : null;

                    var min = tfilter.repeatMin();
                    var max = tfilter.repeatMax();
                    if( min>max )throw new IllegalStateException("min>max");

                    var count = 0;
                    var stop = false;
                    result = true;
                    while( true ){
                        if( ev.isEmpty() ){
                            result = false;
                            stop = true;
                            break;
                        }

                        var nextEv = ev.dropLast();
                        if( tfilter.test(ev) ){
                            count++;
                            if( count>=min ){
                                if( nextFilter!=null && nextFilter.test(nextEv) ){
                                    ev = nextEv;
                                    break;
                                }
                            }
                            if( count==max ){
                                ev = nextEv;
                                break;
                            }
                        }else {
                            if( count<min ){
                                result = false;
                                stop = false;
                            }
                        }

                        ev = nextEv;
                    }

                    if( stop )break;
                }else{
                    if( !filter.test(ev) ){
                        result = false;
                        break;
                    }
                    ev = ev.dropLast();
                }

                if( ev.isEmpty() )break;
            }
            return result;
        };
//        return ev -> {
//            for( int i=0; i<tagz.length; i++ ){
//                var fltr = tagz[i];
//                if( fltr==null )continue;
//
//                int drop = tagz.length-i-1;
//                var ctx = ev.dropLast(drop);
//                var match = fltr.test(ctx);
//                if( !match )return false;
//            }
//            return true;
//        };
    }

    public static Predicate<ReadContext> isStart(){
        return ReadContext::isStartElement;
    }
    public static Predicate<ReadContext> isEnd(){
        return ReadContext::isEndElement;
    }

    public static Predicate<ReadContext> enter(Predicate<ReadContext> follow){
        if( follow==null )throw new IllegalArgumentException( "follow==null" );
        return and( isStart(), follow );
    }

    public static Predicate<ReadContext> exit(Predicate<ReadContext> follow){
        if( follow==null )throw new IllegalArgumentException( "follow==null" );
        return and( isEnd(), follow );
    }

    @SafeVarargs
    public static Predicate<ReadContext> and(Predicate<ReadContext> ... conditions ){
        if( conditions==null )throw new IllegalArgumentException( "conditions==null" );
        if( conditions.length<1 )throw new IllegalArgumentException( "conditions.length<1" );
        for( int i=0;i<conditions.length;i++ ){
            if( conditions[i]==null ){
                throw new IllegalArgumentException("conditions["+i+"]==null");
            }
        }
        return ev -> {
            for( var c : conditions ){
                var r = c.test(ev);
                if( !r )return false;
            }
            return true;
        };
    }

    @SafeVarargs
    public static Predicate<ReadContext> or(Predicate<ReadContext> ... conditions ){
        if( conditions==null )throw new IllegalArgumentException( "conditions==null" );
        if( conditions.length<1 )throw new IllegalArgumentException( "conditions.length<1" );
        for( int i=0;i<conditions.length;i++ ){
            if( conditions[i]==null ){
                throw new IllegalArgumentException("conditions["+i+"]==null");
            }
        }
        return ev -> {
            for( var c : conditions ){
                if( !c.test(ev) )return true;
            }
            return false;
        };
    }
}
