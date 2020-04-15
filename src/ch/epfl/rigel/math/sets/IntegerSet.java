package ch.epfl.rigel.math.sets;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerSet extends IndexedSet<Integer, Integer>{

    final Integer min, max;

    public IntegerSet(Collection<Integer> t) {
        super(t, new SetFunction<>(i -> i));
        min= t.stream().min(Comparator.comparingInt(Integer::intValue)).orElseThrow();
        max= t.stream().max(Comparator.comparingInt(Integer::intValue)).orElseThrow();
    }

    public IntegerSet(Integer min, Integer max) {
        super(IntStream.range(min, max).boxed().collect(Collectors.toList()), new SetFunction<>(i -> i));
        this.min = min;
        this.max = max;
    }


}
