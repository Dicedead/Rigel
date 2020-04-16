package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedPair;

import java.util.Optional;
import java.util.function.Function;

public interface Graph<T, V extends MathSet<T>> {

    Optional<V> getNeighbors(final T point);
    OrderedPair<T> flow(final Function<V, T> chooser, final T point);
    Optional<Iterable<T>> findPathBetween(final T v1, final T v2);
    Graph<T, ? extends MathSet<T>> on(MathSet<T> points);
    Graph<T, V> connectedComponent(final T point);
    MathSet<Graph<T, V>> connectedComponents();
    MathSet<Link<T>> edgeSet();
    V vertexSet();


}
