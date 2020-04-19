package ch.epfl.rigel.math.sets;

import java.util.function.Predicate;

@FunctionalInterface
public interface Equation<T> extends Predicate<T> {
    default MathSet<T> solveIn(MathSet<T> m)
    {
        return m.suchThat(this);
    }

}
