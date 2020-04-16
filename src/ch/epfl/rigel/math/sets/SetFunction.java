package ch.epfl.rigel.math.sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetFunction<T, U> implements Function<MathSet<T>, MathSet<U>> {
    private final Function<MathSet<T>, MathSet<U>> f;
    private final Function<T, U> precise;

    public SetFunction(Function<T, U> f)
    {
        this.f = (S ->  S.stream().map(f).collect(MathSet.toSet()));
        precise = f;
    }
    public MathSet<U> restriction(MathSet<T> set, Collection<T> domain) {

        return set.suchThat(domain::contains).image(precise);
    }


    @Override
    public MathSet<U> apply(MathSet<T> set) {
        return f.apply(set);
    }

    public U applyOn(T t) {
        return precise.apply(t);
    }

}
