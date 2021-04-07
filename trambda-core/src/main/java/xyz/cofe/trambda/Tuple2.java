package xyz.cofe.trambda;

/**
 * Кортеж из двух значений
 * @param <A> тип первого значения
 * @param <B> тип второго значения
 */
public interface Tuple2<A,B> {
    /**
     * Возвращает первое значение
     * @return первое значение
     */
    A a();

    /**
     * Возвращает второе значение
     * @return второе значение
     */
    B b();

    /**
     * Создание кортежа
     * @param a первое значение
     * @param b второе значение
     * @param <A> тип первого значения
     * @param <B> тип второго значения
     * @return кортеж
     */
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
