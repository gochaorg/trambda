package xyz.cofe.trambda;

public interface Tuple2<A,B> {
    A a();
    B b();
    public static <A,B> Tuple2<A,B> of(A a, B b){
        return new Tuple2<A, B>() {
            @Override
            public A a(){
                return a;
            }

            @Override
            public B b(){
                return b;
            }
        };
    }
}
