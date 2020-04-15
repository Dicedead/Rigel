package ch.epfl.rigel.math.sets;

import java.util.function.Function;

public interface Monad<A> {
    <B> Monad<B> map(Monad<A> m, Function<A, Monad<B>> f);
    <B> Monad<B> eval(Function<Monad<A>, Monad<B>> f);
    Monad<A> construct(A a);
}
