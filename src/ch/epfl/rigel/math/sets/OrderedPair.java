package ch.epfl.rigel.math.sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderedPair<T> extends IndexedSet<T, Integer> {

    public OrderedPair(Collection<T> t, SetFunction<Integer, T> indexer) {
        super(t, indexer);
    }

    @SafeVarargs
    public OrderedPair(T... t) {
        super(List.of(t), new SetFunction<>((Integer i) -> t[i]));
    }

    public List<T> toList()
    {
        return IntStream.rangeClosed(0, getData().size()).mapToObj(this::at).collect(Collectors.toList());
    }

    @Override
    public Stream<T> stream()
    {
        return toList().stream();
    }
}
