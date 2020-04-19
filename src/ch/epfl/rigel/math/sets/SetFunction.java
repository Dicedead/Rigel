package ch.epfl.rigel.math.sets;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */

public final class SetFunction<T, U> implements Function<MathSet<T>, MathSet<U>> {
    private final Function<MathSet<T>, MathSet<U>> functionOfSets;
    private final Function<T, U> functionOfElement;

    public SetFunction(Function<T, U> f) {
        this.functionOfSets = S -> S.stream().map(f).collect(MathSet.toMathSet());
        this.functionOfElement = f;
    }

    public MathSet<U> restriction(MathSet<T> set, Collection<T> domain) {
        return set.suchThat(domain::contains).image(functionOfElement);
    }


    @Override
    public MathSet<U> apply(MathSet<T> set) {
        return functionOfSets.apply(set);
    }

    public U applyOn(T t) {
        return functionOfElement.apply(t);
    }

}
