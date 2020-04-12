package ch.epfl.rigel.math.graphs;

import java.util.Set;

public interface Arrow<T> {
    Set<T> getPoints();
    T next (final T t);
}
