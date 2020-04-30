package ch.epfl.rigel.math.sets.properties;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;

import java.util.function.Predicate;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface Equation<T> extends Predicate<T> {

    /**
     * Primary method allowing to retrieve the solution of an equation in a given set
     *
     * @param m the set in which the equation is solved
     * @return The set of all elements satisfying the given equation
     */
    default AbstractMathSet<T> solveIn(AbstractMathSet<T> m) {
        return m.suchThat(this);
    }

    @Override
    default Equation<T> and(Predicate<? super T> other) {
        return t -> test(t) && other.test(t);
    }

    @Override
    default Equation<T> or(Predicate<? super T> other) {
        return t -> test(t) || other.test(t);
    }
}
