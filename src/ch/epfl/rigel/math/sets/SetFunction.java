package ch.epfl.rigel.math.sets;

import com.sun.javafx.geom.transform.Identity;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Function;

import static ch.epfl.rigel.math.sets.MathSet.toMathSet;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
@FunctionalInterface
public interface SetFunction<T, U> extends Function<T, U> {

    default MathSet<U> restriction(MathSet<T> set, Collection<T> domain) {
        return apply(set.suchThat(domain::contains));
    }

    default Equation<T> preImageOf(final MathSet<U> u)
    {
        return (t -> u.contains(apply(t)));
    }

    default Equation<T> preImageOf(final U u)
    {
        return (t -> apply(t).equals(u));
    }

    static <T, U> Equation<T> preImageOf(SetFunction<T, U> f ,final U u)
    {
        return (t -> f.apply(t).equals(u));
    }

    default MathSet<U> apply(MathSet<T> set) {
        return set.stream().map(this).collect(toMathSet());
    }

    @Override
    default <V> SetFunction<V, U> compose(Function<? super V, ? extends T> before) {
        return (v -> apply(before.apply(v)));
    }

    @Override
    default <V> SetFunction<T, V> andThen(Function<? super U, ? extends V> after) {
        return (t -> after.apply(apply(t)));
    }

    static <T> SetFunction<T, T> identity()
    {
        return i -> i;
    }

}
