package ch.epfl.rigel.math.sets;

import java.util.function.Predicate;

@FunctionalInterface
public interface Equation<T> extends Predicate<T> {

    default MathSet<T> solveIn(MathSet<T> m)
    {
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
