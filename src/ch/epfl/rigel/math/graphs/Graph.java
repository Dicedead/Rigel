package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface Graph<T, V extends MathSet<T>> {

    Optional<V> getNeighbours(final T point);
    OrderedSet<T> flow(final Function<V, T> chooser, final T point);
    Graph<T, ? extends MathSet<T>> on(MathSet<T> points);
    Graph<T, V> connectedComponent(final T point);
    MathSet<Graph<T, V>> connectedComponents();
    MathSet<Link<T>> edgeSet();
    V vertexSet();

    default OrderedSet<T> flow(final Comparator<T> chooser, final T point) {
        return flow((V vertices) -> Collections.max(vertices.getData(), chooser), point);
    }

    default boolean areConnected(T v1, T v2)
    {
        return connectedComponent(v1).equals(connectedComponent(v2));
    }

}
