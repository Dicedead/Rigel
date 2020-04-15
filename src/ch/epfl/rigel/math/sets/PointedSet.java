package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.DoubleStream;

public class PointedSet<T> extends MathSet<T> {
    final private T special;
    public PointedSet(Collection<T> t, T special) {
        super(t);
        this.special = special;
    }
    public PointedSet(MathSet<T> t, T special) {
        super(t.getData());
        this.special = special;
    }

    public PointedSet(PointedSet<T> t) {
        super(t.getData());
        this.special = t.special;
    }

    public T getSpecial()
    {return special;}

    public <U> PointedSet<Pair<T, U>> directSum(PointedSet<U> other) {
        return new PointedSet<>(super.directSum(other, special, other.getSpecial()), new Pair<>(special, other.getSpecial()));
    }

    public <U> PointedSet<Pair<T, U>> directSum(Collection<PointedSet<U>> other) {
        return new PointedSet<>(
                other.stream().map(s -> super.directSum(s, special, s.getSpecial())).collect(union()),
                        new Pair<>(special, other.iterator().next().getSpecial()));
    }

}