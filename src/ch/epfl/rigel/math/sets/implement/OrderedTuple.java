package ch.epfl.rigel.math.sets.implement;

import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.properties.SetFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of a set indexed by integers
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class OrderedTuple<T> extends IndexedSet<T, Integer> implements AbstractOrderedTuple<T> {

    /**
     * Main constructor allowing to construct an OrderedTuple from any Iterable
     *
     * @param t the iterable to convert
     */
    public OrderedTuple(Iterable<T> t) {
        this(iterableToList(t));
    }

    /**
     * Secondary constructor from list
     *
     * @param t list to convert
     */
    public OrderedTuple(List<T> t) {
        this(t, t::get);
    }

    /**
     * Classic constructor for an IndexedSet
     *
     * @param t the underlying data
     * @param indexer the function allowing the order
     */
    public OrderedTuple(Collection<T> t, SetFunction<Integer, T> indexer) {
        super(t, indexer);
    }

    /**
     * Alternate constructor from an array, assigns each elements to its position in the array
     *
     * @param t the array to convert
     */
    @SafeVarargs
    public OrderedTuple(T... t) {
        this(List.of(t), i -> t[i]);
    }

    private static<T> List<T> iterableToList(final Iterable<T> i)
    {
        final List<T> target = new ArrayList<>();
        i.forEach(target::add);
        return target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> OrderedTuple<U> image(SetFunction<T, U> f) {
        return new OrderedTuple<>(toList().stream().map(f).collect(Collectors.toList()));
    }
}
