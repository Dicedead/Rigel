package ch.epfl.rigel.math.sets;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class OrderedTuple<T> extends IndexedSet<T, Integer>{

    /**
     * Classic constructor for an IndexedSet
     * @param t the underlying data
     * @param indexer the function allowing the order
     */
    public OrderedTuple(Collection<T> t, SetFunction<Integer, T> indexer) {
        super(t, indexer);
    }

    /**
     * The constructor from an array, assign each elements to its position in the array
     * @param t the array to convert
     */
    @SafeVarargs
    public OrderedTuple(final T... t) {
        super(List.of(t),  i -> t[i]);
    }

    /**
     * Constructor allowing to construct an OrderedTuple from any Iterable
     * @param t the iterable to convert
     */
    public OrderedTuple(final Iterable<T> t) {
        this(iterableToList(t));
    }

    private static<T> List<T> iterableToList(final Iterable<T> i)
    {
        final List<T> target = new ArrayList<>();
        i.forEach(target::add);
        return target;
    }

    public OrderedTuple(final List<T> t) {
        super(t, t::get);
    }

    /**
     * @return the OrderedTuple as a List
     */
    public List<T> toList() {
        return IntStream.range(0, getData().size()).mapToObj(this::at).collect(Collectors.toList());
    }
    /**
     * @param t the starting element
     * @return the element right after it
     */
    public T next (T t)
    {
        return at(toList().indexOf(t) + 1);
    }

    /**
     * @param t the starting element
     * @return the element right behind it
     */
    public T prev (T t)
    {
        return at(toList().indexOf(t) - 1);
    }

    /**
     *
     * @param t the element to locate
     * @return The index of t in this OrderedTuple
     */
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

    /**
     * Identifies an orderedTuple of OrderedTuple with a flat OrderedTuple
     * @param t the OrderedTuple to flatten
     * @return An OrderedTuple containing every element of each OrderedTuple of t
     */
    public OrderedTuple<T> identification(OrderedTuple<OrderedTuple<T>> t)
    {
        List<T> l = new ArrayList<>();
        t.forEach(o -> l.addAll(o.toList()));
        return new OrderedTuple<>(l);
    }

}
