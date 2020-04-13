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
public final class Graph<T> extends AbstractGraph<T, UndirectedLink<T>> {



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
     * Main Graph Constructor: takes a set of vertices and a set of undirectedLinks
     *
     * @param points (Set<T>) set of vertices
     * @param lines (Set<UndirectedLink<T>>) set of undirected links
     */

    public Graph(Set<T> points, Set<UndirectedLink<T>> lines) {
        super(points, lines);
    }
}
