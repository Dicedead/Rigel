package ch.epfl.rigel.math.sets.properties;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.implement.MathSet;

import java.util.function.Function;

import static ch.epfl.rigel.math.sets.implement.MathSet.toMathSet;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface SetFunction<T, U> extends Function<T, U> {

    /**
     * @param u the co-domain set
     * @return An equation locating all elements that will be mapped in u by this function
     */
    default Equation<T> preImageOf(AbstractMathSet<U> u)
    {
        return (t -> u.contains(apply(t)));
    }

    /**
     * @param u the codomain element
     * @return An equation locating all elements that will be mapped at u by this function
     */
    default Equation<T> preImageOf(U u)
    {
        return (t -> apply(t).equals(u));
    }

    /**
     * Lifter from element function to set function
     * @param set the set on which to apply the function
     * @return the set containing the image of all elements of set by this function
     */
    default MathSet<U> apply(AbstractMathSet<T> set) {
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

    /**
     * The identity function
     * @param <T> Whatever type wanted
     * @return a function doing nothing
     */
    static <T> SetFunction<T, T> identity()
    {
        return i -> i;
    }

}
