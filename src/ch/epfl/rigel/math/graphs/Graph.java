package ch.epfl.rigel.math.graphs;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of an undirected graph
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Graph<T> extends AbstractGraph<T, UndirectedLink<T>> {

    /**
     * Main Graph Constructor: takes a set of vertices and a set of undirectedLinks
     *
     * @param points (Set<T>) set of vertices
     * @param lines (Set<UndirectedLink<T>>) set of undirected links
     */
    public Graph(Set<T> points, Set<UndirectedLink<T>> lines) {
        super(points, lines);
    }

    /**
     * Alternate Graph constructor: takes a set of vertices and links every vertex with all the other vertices
     *
     * @param points (Set<T>) given said of vertices
     */
    public Graph(Set<T> points)
    {
        super(points, points.stream()
                .map(point1 -> points.stream()
                        .map(point2 -> new UndirectedLink<T>(point1, point2)).collect(Collectors.toCollection(HashSet::new)))
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * Finds and creates the undirected graph ON given set of vertices 'points' if 'points' is included in the set of
     * vertices - in other words, a subgraph of this graph
     *
     * @param points (Set<T>)
     * @return (AbstractGraph<T, UndirectedLink<T>>) with Graph<T> implementation
     */
    @Override
    public AbstractGraph<T, UndirectedLink<T>> on(Set<T> points) {
        if (getPointSet().containsAll(points))
            return new Graph<>(points, getEdgeSet().stream().filter(link -> points.containsAll(link.getPoints()))
                    .collect(Collectors.toCollection(HashSet::new)));
        else throw new NoSuchElementException("Not all points in given set of points in vertices set.");
    }
}
