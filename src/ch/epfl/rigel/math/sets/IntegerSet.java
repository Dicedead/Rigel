package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IntegerSet extends IndexedSet<Integer, Integer> {

    private final Integer min, max;

    public IntegerSet(Collection<Integer> t) {
        super(t, new SetFunction<>(i -> i));

        Preconditions.checkArgument(t.size() != 0);
        min = t.stream().min(Comparator.comparingInt(Integer::intValue)).get();
        max = t.stream().max(Comparator.comparingInt(Integer::intValue)).get();
    }

    public IntegerSet(Integer min, Integer max) {
        super(IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList()), new SetFunction<>(i -> i));
        this.min = min;
        this.max = max;
    }
}
