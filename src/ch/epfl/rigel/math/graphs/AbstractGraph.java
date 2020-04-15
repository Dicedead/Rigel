package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedPair;
import javafx.util.Pair;

import java.util.List;
import java.util.function.Function;


public interface AbstractGraph<T, V extends MathSet<T>, E extends MathSet<Link<T>>> {

    boolean hasNeighbors(final T point);
    V getNeighbors(final T point);
    Iterable<T> flow(final Function<V, T> chooser, final T point);
    AbstractGraph<T, V, E> union(AbstractGraph<T, V, E> otherGraph);
    AbstractGraph<T, V, E> intersection(AbstractGraph<T, V, E> otherGraph);
    AbstractGraph<T, V, E> minus(AbstractGraph<T, V, E> otherGraph);
    boolean contains(final T v);
    boolean containsEdge(final Link<T> e);
    Iterable<T> findPathBetween(final T v1, final T v2);
    Link<T> of(final T value1, final T value2);
    AbstractGraph<T, V, E> on(AbstractGraph<T, V, E> points);
    AbstractGraph<T, V, E> on(V points);
    MathSet<AbstractGraph<T, V, E>> components();
    AbstractGraph<T, V, E> component(final T point);
    E edgeSet();
    V vertexSet();


}
