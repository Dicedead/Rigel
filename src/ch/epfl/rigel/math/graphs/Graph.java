package ch.epfl.rigel.math.graphs;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Graph<T> extends AbstractGraph<T,Link<T>> {

    public Graph(Set<T> points)
    {
        super(points, points.stream()
                .map(l -> points.stream()
                        .map(j -> new Link<T>(l, j)).collect(Collectors.toSet()))
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(HashSet::new)));

    }

    public Graph(Set<T> points, Set<Link<T>> lines) {
        super(points, lines);
    }

    @Override
    public AbstractGraph<T, Link<T>> on(Set<T> points) {
        if (vertexSet.containsAll(points))
            return new Graph<>(points, edgeSet.stream().filter(l -> points.containsAll(l.getPoints())).collect(Collectors.toSet()));

        else throw new NoSuchElementException();
    }
}
