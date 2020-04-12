package ch.epfl.rigel.math.graphs;

import java.util.Set;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface Arrow<T> {
    Set<T> getPoints();
    T next (final T t);
}
