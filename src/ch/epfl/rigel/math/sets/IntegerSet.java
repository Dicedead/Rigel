package ch.epfl.rigel.math.sets;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerSet extends PointedSet<Integer>{

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    private final Integer min, max;

    public IntegerSet(Integer min, Integer max) {
        super(IntStream.range(min, max).boxed().collect(Collectors.toList()), max);
        this.min = min;
        this.max = max;
    }
    public IntegerSet imageInt(Function<Integer, Integer> f) {
        return new IntegerSet(super.image(f).stream().min(Comparator.comparingInt(Integer::valueOf)).orElseThrow(),
                super.image(f).stream().max(Comparator.comparingInt(Integer::valueOf)).orElseThrow());
    }
}

