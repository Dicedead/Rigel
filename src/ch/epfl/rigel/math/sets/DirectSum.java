package ch.epfl.rigel.math.sets;

import java.util.Collection;
import java.util.function.Function;

public class DirectSum<U> extends MathSet<U> implements Function<MathSet<U>, MathSet<U>> {

    public DirectSum(Collection<U> t) {
        super(t);
    }

    public DirectSum(MathSet<U> t) {
        super(t);
    }

    @Override
    public MathSet<U> apply(MathSet<U> uMathSet) {
        return null;
    }
}
