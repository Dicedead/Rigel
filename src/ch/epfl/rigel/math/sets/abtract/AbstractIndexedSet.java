package ch.epfl.rigel.math.sets.abtract;

public interface AbstractIndexedSet<T, I> extends AbstractMathSet<T> {
    SetFunction<I, T> getIndexer();
    default T at(I i) {
        return getIndexer().apply(i);
    }

}
