package ch.epfl.rigel.math.sets;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class IndexedSet<T, I> extends MathSet<T> {

    private final SetFunction<I, T> indexer;

    public IndexedSet(final Collection<T> t, final SetFunction<I, T> indexer) {
        super(t);
        this.indexer = indexer;
    }

    public IndexedSet(final MathSet<T> t, final SetFunction<I, T> indexer) {
        super(t);
        this.indexer = indexer;
    }

    public T at(I i) {
        return indexer.apply(i);
    }

    public IndexedSet<T, I> indexedUnion(Collection<I> i) {
        return new IndexedSet<>(i.stream().map(this::at).collect(Collectors.toSet()), indexer);
    }


    public <U> IndexedSet<U, I> image(SetFunction<T, U> f) {
        return new IndexedSet<>(f.apply(this), (i -> f.apply(at(i))));
    }

}
