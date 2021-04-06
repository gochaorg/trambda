package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import xyz.cofe.trambda.Tuple2;
import xyz.cofe.trambda.bc.MethodDef;

public class SecurFilters<MESSAGE,SCOPE> {
    private final List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators;
    public SecurFilters(List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators){
        List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators00 = new ArrayList<>();
        if( validators!=null ){
            validators00.addAll(validators);
        }
        this.validators = Collections.unmodifiableList(validators00);
    }

    public List<SecurMessage<MESSAGE,SCOPE>> validate( List<SecurAccess<?,SCOPE>> secur ){
        if( secur==null )throw new IllegalArgumentException( "secur==null" );

        List<SecurMessage<MESSAGE,SCOPE>> res = new ArrayList<>();
        var validtrs = validators;
        if( validtrs!=null ){
            for( var sa : secur ){
                if( sa==null )continue;

                MESSAGE msg = null;
                Boolean allw = null;
                for( var vald : validtrs ){
                    if( vald==null )continue;
                    var omsg = vald.apply(sa);
                    if( omsg.isPresent() ){
                        msg = omsg.get().a();
                        allw = omsg.get().b();
                        break;
                    }
                }

                if( msg!=null ){
                    res.add(new SecurMessage<>(sa,allw,msg));
                }
            }
        }

        return res;
    }
    public static <MESSAGE,SCOPE> Builder<MESSAGE,SCOPE> create(Class<MESSAGE> cMsg, Class<SCOPE> cScope){
        return new Builder<>();
    }
    public static Builder<String, MethodDef> create(){
        return create(String.class, MethodDef.class);
    }
    public static class Builder<MESSAGE,SCOPE> {
        private final List<Function<SecurAccess<?,SCOPE>, Optional<Tuple2<MESSAGE,Boolean>>>> validators = new ArrayList<>();
        public SecurFilters<MESSAGE,SCOPE> build(){
            return new SecurFilters<>(validators);
        }

        public AllowBuilder<MESSAGE,SCOPE> allow(){
            return new AllowBuilder<>(this);
        }

        public Builder<MESSAGE,SCOPE> allow(Consumer<AllowBuilder<MESSAGE,SCOPE>> conf){
            if( conf==null )throw new IllegalArgumentException( "conf==null" );
            conf.accept(new AllowBuilder<>(this));
            return this;
        }

        public DenyBuilder<MESSAGE,SCOPE> deny(){
            return new DenyBuilder<>(this);
        }

        public Builder<MESSAGE,SCOPE> deny(Consumer<DenyBuilder<MESSAGE,SCOPE>> conf){
            if( conf==null )throw new IllegalArgumentException( "conf==null" );
            conf.accept( new DenyBuilder<>(this) );
            return this;
        }
    }

    public abstract static class PredicateBuilder<MESSAGE,SCOPE,SELF extends PredicateBuilder<MESSAGE,SCOPE,SELF>> {
        public final Builder<MESSAGE,SCOPE> builder;
        public PredicateBuilder(Builder<MESSAGE,SCOPE> builder){
            this.builder = builder;
        }

        protected abstract void append(Function<SecurAccess<?,SCOPE>,Optional<MESSAGE>> filter);

        public Builder<MESSAGE,SCOPE> any(MESSAGE message) {
            append( t -> Optional.of(message) );
            return builder;
        }

        public Builder<MESSAGE,SCOPE> methodCall(Predicate<MethodCall> filter, MESSAGE message) {
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof MethodCall ){
                    if( filter.test((MethodCall) ev) ){
                        return Optional.of(message);
                    };
                }
                return Optional.empty();
            });
            return builder;
        }
        public Builder<MESSAGE,SCOPE> indyCall(Predicate<InvokeDynamicCall> filter, MESSAGE message) {
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof InvokeDynamicCall ){
                    return filter.test((InvokeDynamicCall) ev) ? Optional.of(message) : Optional.empty();
                }
                return Optional.empty();
            });
            return builder;
        }
        public Builder<MESSAGE,SCOPE> call(Predicate<Call<?>> filter, MESSAGE message) {
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof Call ){
                    return filter.test((Call<?>) ev) ? Optional.of(message) : Optional.empty();
                }
                return Optional.empty();
            });
            return builder;
        }
        public Builder<MESSAGE,SCOPE> field(Predicate<FieldAccess> filter, MESSAGE message){
            if( filter==null )throw new IllegalArgumentException( "filter==null" );
            append( ev -> {
                if( ev instanceof FieldAccess ){
                    return filter.test((FieldAccess) ev) ? Optional.of(message) : Optional.empty();
                }
                return Optional.empty();
            });
            return builder;
        }
    }

    public static class AllowBuilder<MESSAGE,SCOPE> extends PredicateBuilder<MESSAGE,SCOPE,AllowBuilder<MESSAGE,SCOPE>> {
        public AllowBuilder(Builder<MESSAGE,SCOPE> builder){
            super(builder);
        }

        @Override
        protected void append(Function<SecurAccess<?,SCOPE>,Optional<MESSAGE>> filter){
            builder.validators.add( ev -> {
                var res = filter.apply(ev);
                return res.map(message -> Tuple2.of(message, true));
            });
        }
    }
    public static class DenyBuilder<MESSAGE,SCOPE> extends PredicateBuilder<MESSAGE,SCOPE, DenyBuilder<MESSAGE,SCOPE>> {
        public DenyBuilder(Builder<MESSAGE,SCOPE> builder){
            super(builder);
        }

        @Override
        protected void append(Function<SecurAccess<?,SCOPE>,Optional<MESSAGE>> filter){
            builder.validators.add( ev -> {
                var res = filter.apply(ev);
                return res.map(message -> Tuple2.of(message, false));
            });
        }
    }
}
