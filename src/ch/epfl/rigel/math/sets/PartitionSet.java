package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PartitionSet<T> extends MathSet<T> {

    private final IndexedSet<MathSet<T>, T> components;

    public PartitionSet(Collection<MathSet<T>> t) {
        super(unionOf(t).getData());
        components = new IndexedSet<>(t, new SetFunction<>(u -> t.stream().filter((s -> s.in(u))).findFirst().orElseThrow()));
    }
    public PartitionSet(IndexedSet<MathSet<T>, T> t) {
        super(unionOf(t.getData()));
        components = t;
    }


    public PartitionSet(final MathSet<T> t, BiFunction<T, T, Boolean> link) {
        this(t.stream().map(l -> t.suchThat(u -> link.apply(l,u))).collect(Collectors.toSet()));
    }

    public PartitionSet(final MathSet<T> t) {
        this(Collections.singletonList(t));
    }

    public MathSet<T> component(T t) {
        return components.at(t);
    }

    public MathSet<MathSet<T>> components() {
        return components;
    }

    @Override
    public <U> PartitionSet<U> image(Function<T, U> f) {
        return new PartitionSet<U>(components.stream().map(C -> C.image(f)).collect(Collectors.toSet()));
    }
}
