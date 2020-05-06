package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.implement.OrderedTuple;
import ch.epfl.rigel.math.sets.properties.SetFunction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static ch.epfl.rigel.math.sets.implement.MathSet.emptySet;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface Graph<T, V extends AbstractMathSet<T>> {
    /**
     * Gets the set of points linked to given point
     *
     * @param point (T) given point
     * @return (Set < T >) said set
     */
    Optional<V> getNeighbours(T point);

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
    Graph<T, V> connectedComponent(T point);

    /**
     * @return the Set of connected components of this graph
     */
    AbstractMathSet<Graph<T, V>> connectedComponents();

    /**
     * @return (AbstractMathSet < Link < T > >) getter for immutable set of edges
     */
    AbstractMathSet<Link<T>> edgeSet();

    /**
     * @return (AbstractMathSet < T >) getter for immutable set of vertices
     */
    V vertexSet();

    /**
     * Allows to navigate the graph by making a choice at each step
     *
     * @param chooser the choice function
     * @param point   the point to begin with
     * @return the OrderedTuple of points traversed by the choice function
     */
    default AbstractOrderedTuple<T> flow(Comparator<T> chooser, T point) {
        return flow((V vertices) -> Collections.max(vertices.getRawData(), chooser), point);
    }

    /**
     * Allows to navigate the graph by making a choice at each step
     *
     * @param chooser the choice function
     * @param point   the point to begin with
     * @return the List of points traversed by the choice function
     */
    default AbstractOrderedTuple<T> flow(SetFunction<V, T> chooser, T point) {
        if (getNeighbours(point).isEmpty())
            return new OrderedTuple<>(point);
        final List<T> flowList = flow(chooser, chooser.apply(getNeighbours(point).get())).toList();
        flowList.add(point);
        Collections.reverse(flowList);
        return new OrderedTuple<>(flowList);
    }


    default AbstractMathSet<T> neighboursOf(AbstractMathSet<T> t) {
        SetFunction<T, Optional<V>> f = this::getNeighbours;

        var a = f.andThen(Optional::isPresent).preImageOf(true).solveIn(t);
        if (a.cardinality() == 0)
            return emptySet();

        var b = AbstractMathSet.unionOf(a.image(f.andThen(Optional::get)));
        return a.union(neighboursOf(b));
    }

    default boolean areConnected(T v1, T v2) {
        return connectedComponent(v1).equals(connectedComponent(v2));
    }

}
