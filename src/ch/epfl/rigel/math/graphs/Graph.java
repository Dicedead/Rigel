package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public interface Graph<V, E extends MathSet<V>> {

    Optional<E> getNeighbours(final V point);
    OrderedSet<V> flow(final Function<E, V> chooser, final V point);
    Optional<Iterable<V>> findPathBetween(final V v1, final V v2);
    Graph<V, ? extends MathSet<V>> on(MathSet<V> points);
    Graph<V, E> connectedComponent(final V point);
    MathSet<Graph<V, E>> connectedComponents();
    MathSet<Link<V>> edgeSet();
    E vertexSet();

    default OrderedSet<V> flow(final Comparator<V> chooser, final V point) {
        return flow((E edges) -> Collections.max(edges.getData(), chooser), point);
    }

}
