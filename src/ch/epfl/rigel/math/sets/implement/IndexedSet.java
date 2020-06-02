package ch.epfl.rigel.math.sets.implement;

import ch.epfl.rigel.math.sets.abstraction.AbstractIndexedSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.properties.SetFunction;

import java.util.Collection;
import java.util.Map;

/**
 * Implementation of a set equipped with an indexing function
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class IndexedSet<T, I> extends MathSet<T> implements AbstractIndexedSet<T, I> {

    private final SetFunction<I, T> indexer;

    /**
     * Main IndexedSet constructor from mathset
     *
     * @param t (AbstractMathSet<T>) data
     * @param indexer (SetFunction<I, T>) indexing function
     */
    public IndexedSet(AbstractMathSet<T> t, SetFunction<I, T> indexer) {
        super(t);
        this.indexer = indexer;
    }

    /**
     * Alternate IndexedSet from Map
     *
     * @param t (Map<I, T>) the values of this map will become the elements in this indexed set, the key->value
     *          associations will be kept as indexing function
     */
    public IndexedSet(Map<I, T> t) {
        super(t.values());
        this.indexer = t::get;
    }

    /**
     * Alternate IndexedSet constructor from Collection
     *
     * @param t (Collection<T>) elements
     * @param indexer (SetFunction<I, T>) indexing function
     */
    public IndexedSet(Collection<T> t, SetFunction<I, T> indexer) {
        super(t);
        this.indexer = indexer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SetFunction<I, T> getIndexer() {
        return indexer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> IndexedSet<U, I> image(SetFunction<T, U> f) {
        return new IndexedSet<>(f.apply(this), (i -> f.apply(at(i))));
    }
}
