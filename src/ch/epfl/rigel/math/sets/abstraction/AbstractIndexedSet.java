package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.math.sets.properties.SetFunction;

/**
 * Abstraction of an indexed set
 * @see ch.epfl.rigel.math.sets.implement.IndexedSet
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface AbstractIndexedSet<T, I> extends AbstractMathSet<T> {

    /**
     * @return (SetFunction<I, T>) get the indexing function for this set
     */
    SetFunction<I, T> getIndexer();

    /**
     * @param i (I) index
     * @return (T) value at index i
     */
    default T at(I i) {
        return getIndexer().apply(i);
    }

}
