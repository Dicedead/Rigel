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
        return indexer.applyOn(i);
    }

    public IndexedSet<T, I> indexedUnion(Collection<I> i) {
        return new IndexedSet<>(i.stream().map(this::at).collect(Collectors.toSet()), indexer);
    }


    public <U> IndexedSet<U, I> image(Function<T, U> f) {
        return new IndexedSet<>(stream().map(f).collect(Collectors.toSet()), new SetFunction<>(lift(f)));
    }

    public <U> Function<I, U> lift(Function<T, U> f) {
        return i -> f.apply(at(i));
    }

}
