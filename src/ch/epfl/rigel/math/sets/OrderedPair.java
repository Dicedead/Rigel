package ch.epfl.rigel.math.sets;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderedPair<T> extends IndexedSet<T, Integer> implements Iterable<T>{

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

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        toList().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return toList().spliterator();
    }

}
