package ch.epfl.rigel.math.sets.concrete;

import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.math.sets.abtract.AbstractIndexedSet;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.abtract.SetFunction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class IndexedSet<T, I> extends MathSet<T> implements AbstractIndexedSet<T, I> {

    private final SetFunction<I, T> indexer;

    public IndexedSet(Collection<T> t, final SetFunction<I, T> indexer) {
        super(t);
        this.indexer = indexer;
    }

    public IndexedSet(final Map<I, T> t) {
        super(t.values());
        this.indexer = t::get;
    }

    public IndexedSet(final AbstractMathSet<T> t, final SetFunction<I, T> indexer) {
        super(t);
        this.indexer = indexer;
    }

    @Override
    public SetFunction<I, T> getIndexer() {
        return indexer;
    }

    @Override
    public <U> IndexedSet<U, I> image(SetFunction<T, U> f) {
        return new IndexedSet<>(f.apply(this), (i -> f.apply(at(i))));
    }

}
