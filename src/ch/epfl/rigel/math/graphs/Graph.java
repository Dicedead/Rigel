package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.abtract.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.OrderedTuple;
import ch.epfl.rigel.math.sets.abtract.SetFunction;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface Graph<T, V extends AbstractMathSet<T>> {
    /**
     * Gets the set of points linked to given point
     *
     * @param point (T) given point
     * @return (Set <T>) said set
     */
    Optional<V> getNeighbours(final T point);
    /**
     * Allows to navigate the graph by making a choice at each step
     *
     * @param chooser the choice function
     * @param point   the point to begin with
     * @return the List of points traversed by the choice function
     */
    AbstractOrderedTuple<T> flow(final SetFunction<V, T> chooser, final T point);
    /**
     * Creates a graph ON given set of vertices
     *
     * @param points (Set<T>)
     * @return (Graph < T, U >) some implementation of Graph<T,U>
     */
    Graph<T, ? extends AbstractMathSet<T>> on(AbstractMathSet<T> points);
    /**
     * A component is a maximally connected subset of a graph
     *
     * @param point the point on which we want the component
     * @return the component onn which this point lies
     */
    Graph<T, V> connectedComponent(final T point);
    /**
     * @return the Set of connected components of this graph
     */
    AbstractMathSet<Graph<T, V>> connectedComponents();
    /**
     * @return (AbstractMathSet<Link<T>>) getter for immutable set of edges
     */
    AbstractMathSet<Link<T>> edgeSet();
    /**
     * @return (AbstractMathSet<T>) getter for immutable set of vertices
     */
    V vertexSet();

    /**
     * Allows to navigate the graph by making a choice at each step
     *
     * @param chooser the choice function
     * @param point   the point to begin with
     * @return the OrderedTuple of points traversed by the choice function
     */
    default AbstractOrderedTuple<T> flow(final Comparator<T> chooser, final T point) {
        return flow((V vertices) -> Collections.max(vertices.getData(), chooser), point);
    }

    default boolean areConnected(T v1, T v2)
    {
        return connectedComponent(v1).equals(connectedComponent(v2));
    }

}
