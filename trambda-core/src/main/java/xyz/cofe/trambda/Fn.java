package xyz.cofe.trambda;

import java.io.Serializable;
import java.util.function.Function;

public interface Fn<A,Z> extends Serializable, Function<A,Z> {
    public Z apply(A a);
}
