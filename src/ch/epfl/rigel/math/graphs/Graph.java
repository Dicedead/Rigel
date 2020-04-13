package ch.epfl.rigel.math.graphs;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph<T> extends AbstractGraph<T,Link<T>> {

    public Graph(Set<T> points)
    {
        super(points, points.stream()
                .map(l -> points.stream()
                        .map(j -> new Link<T>(l, j)).collect(Collectors.toSet()))
                .flatMap(Set::stream)
                .collect(Collectors.toSet()));

    }

    public Graph(Set<T> points, Set<Link<T>> lines) {
        super(points, lines);
    }
}
