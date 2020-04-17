package ch.epfl.rigel.math.sets;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OrderedSet<T> extends IndexedSet<T, Integer> implements Iterable<T>{

    public OrderedSet(Collection<T> t, SetFunction<Integer, T> indexer) {
        super(t, indexer);
    }

    @SafeVarargs
    public OrderedSet(final T... t) {
        super(List.of(t), new SetFunction<>((Integer i) -> t[i]));
    }

    public OrderedSet(final Iterable<T> t) {
        this(ittoList(t));
    }

    private  static<T>  List<T> ittoList (final Iterable<T> i)
    {
        final List<T> target = new ArrayList<>();
        i.forEach(target::add);
        return target;
    }

    public OrderedSet(final List<T> t) {
        super(t, new SetFunction<>(t::get));
    }

    public OrderedSet(MathSet<T> points, SetFunction<Integer, T> integerTSetFunction) {
        super(points, integerTSetFunction);
    }

    public List<T> toList() {
        return IntStream.range(0, getData().size()).mapToObj(this::at).collect(Collectors.toList());
    }

    public T next (T t)
    {
        return at(toList().indexOf(t) + 1);
    }

    public T prev (T t)
    {
        return at(toList().indexOf(t) - 1);
    }

    public int indexOf(T t)
    {
        return toList().indexOf(t);
    }

    @Override
    public Stream<T> stream() {
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
