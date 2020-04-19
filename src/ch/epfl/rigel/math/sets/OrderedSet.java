package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.math.graphs.Path;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class OrderedSet<T> extends IndexedSet<T, Integer> implements Iterable<T>{

    public OrderedSet(Collection<T> t, SetFunction<Integer, T> indexer) {
        super(t, indexer);
    }

    public OrderedSet(MathSet<T> points, SetFunction<Integer, T> integerTSetFunction) {
        super(points, integerTSetFunction);
    }
    @SafeVarargs
    public OrderedSet(final T... t) {
        super(List.of(t),  i -> t[i]);
    }

    public OrderedSet(final Iterable<T> t) {
        this(iterableToList(t));
    }

    private static<T> List<T> iterableToList(final Iterable<T> i)
    {
        final List<T> target = new ArrayList<>();
        i.forEach(target::add);
        return target;
    }

    public OrderedSet(final List<T> t) {
        super(t, t::get);
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

    @Override
    public String toString() {
        return this.toList().toString();
    }

    public OrderedSet<T> identification(OrderedSet<OrderedSet<T>> t)
    {
        List<T> l = new ArrayList<>();
        t.forEach(o -> l.addAll(o.toList()));
        return new OrderedSet<T>(l);
    }
}
