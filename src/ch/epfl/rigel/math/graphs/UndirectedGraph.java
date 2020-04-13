package ch.epfl.rigel.math.graphs;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of an undirected graph
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class UndirectedGraph<T> extends Graph<T, UndirectedLink<T>> {
    /*
     Although unused, this class has been left for completeness.
     */

    /**
     * Alternate UndirectedGraph constructor: takes a set of vertices and links every vertex with all the other vertices
     *
     * @param points (Set<T>) given said of vertices
     */
    public UndirectedGraph(Set<T> points)
    {
        super(points, points.stream()
                .map(point1 -> points.stream()
                        .map(point2 -> new UndirectedLink<T>(point1, point2)).collect(Collectors.toCollection(HashSet::new)))
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * Main UndirectedGraph Constructor: takes a set of vertices and a set of undirectedLinks
     *
     * @param points (Set<T>) set of vertices
     * @param lines (Set<UndirectedLink<T>>) set of undirected links
     */
    public UndirectedGraph(Set<T> points, Set<UndirectedLink<T>> lines) {
        super(points, lines);
    }

    /**
     * Returns the smallest sub-graph of this graph containing the parameter vertices
     *
     * @param points (Set<T>) given vertices
     * @return (Graph<T, UndirectedLink<T>>) implemented as an UndirectedGraph<T>
     */
    @Override
    public Graph<T, UndirectedLink<T>> on(Set<T> points) {
        return intersection(new UndirectedGraph<>(points));
    }

    @Override
    public Path<T> findPathBetween(T t, T u) {
        throw new UnsupportedOperationException("Finding a directed path does not make sense in an undirected graph.");
    }

    /**
     * @see Graph#intersection(Graph)
     */
    @Override
    public Graph<T, UndirectedLink<T>> intersection(Graph<T, UndirectedLink<T>> otherGraph) {
        final Set<T> interNodes = otherGraph.getPointSet();
        interNodes.retainAll(this.getPointSet());
        final Set<UndirectedLink<T>> interLinks = otherGraph.getEdgeSet();
        return new UndirectedGraph<T>(interNodes, interLinks);
    }
}
