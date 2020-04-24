package ch.epfl.rigel.math.sets;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class OrderedTuple<T> extends IndexedSet<T, Integer> implements Iterable<T> {

    private final List<T> listEquivalent;
    private final Map<T, Integer> indicesMap;

    public OrderedTuple(MathSet<T> points, SetFunction<Integer, T> integerTSetFunction) {
        super(points, integerTSetFunction);
        this.listEquivalent = Collections.unmodifiableList((List<? extends T>) IntStream.range(0, getData().size())
                .mapToObj(this::at)
                .collect(Collectors.toCollection(ArrayList::new)));
        this.indicesMap = Collections.unmodifiableMap(IntStream.range(0, listEquivalent.size()).boxed()
                .collect(Collectors.toMap(listEquivalent::get, Function.identity(), (o1, o2) -> o1)));
    }

    public OrderedTuple(Collection<T> t, SetFunction<Integer, T> indexer) {
        this(new MathSet<T>(t), indexer);
    }

    @SafeVarargs
    public OrderedTuple(final T... t) {
        this(MathSet.of(t), i -> t[i]);
    }

    public OrderedTuple(final List<T> t) {
        this(t, t::get);
    }

    public OrderedTuple(final Iterable<T> t) {
        this(iterableToList(t));
    }

    private static <T> List<T> iterableToList(final Iterable<T> iter) {
        final List<T> target = new ArrayList<>();
        iter.forEach(target::add);
        return target;
    }

    public List<T> toList() {
        return listEquivalent;
    }

    public T next(T t) {
        return at(indexOf(t) + 1);
    }

    public T prev(T t) {
        return at(indexOf(t) - 1);
    }

    public int indexOf(T t) {
        return indicesMap.get(t);
    }

    @Override
    public Stream<T> stream() {
        return listEquivalent.stream();
    }

    @Override
    public Iterator<T> iterator() {
        return listEquivalent.listIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        listEquivalent.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return listEquivalent.spliterator();
    }

    @Override
    public String toString() {
        return this.listEquivalent.toString();
    }

    public OrderedTuple<T> identification(OrderedTuple<OrderedTuple<T>> t) {
        List<T> l = new ArrayList<>();
        t.forEach(o -> l.addAll(o.listEquivalent));
        return new OrderedTuple<>(l);
    }

}
