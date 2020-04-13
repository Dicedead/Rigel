package ch.epfl.rigel.math.graphs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstraction of a graph, a set of a set of edges and a set of vertices
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
abstract class AbstractGraph<T, U extends AbstractLink<T>> {

    private final Set<T> vertexSet;
    private final Set<U> edgeSet;

    /**
     * Main AbstractGraph constructor
     *
     * @param points (Set<T>) set of vertices
     * @param lines (Set<U>) set of edges
     */
    public AbstractGraph(final Set<T> points, final Set<U> lines) {
        vertexSet = Set.copyOf(points);
        edgeSet = Set.copyOf(lines);
    }

    /**
     * Compositing AbstractGraph constructor, putting many graphs into one
     *
     * @param l (List<AbstractGraph<T, U>>) collection of graphs to be united
     */
    public AbstractGraph(Collection<AbstractGraph<T, U>> l) {
        vertexSet = Set.copyOf(l.stream().flatMap(i -> i.getPointSet().stream()).collect(Collectors.toCollection(HashSet::new)));
        edgeSet = Set.copyOf(l.stream().flatMap(i -> i.getEdgeSet().stream()).collect(Collectors.toCollection(HashSet::new)));
    }

    //TODO: doc
    public AbstractGraph<T, U> component(final T point) {
        return on(vertexSet.stream().map(v -> isConnectedTo(point, v)).flatMap(p -> p.getPointSet().stream())
                .collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * Creates a graph ON given set of vertices
     *
     * @param points (Set<T>)
     * @return (AbstractGraph<T, U>) some implementation of AbstractGraph<T,U>
     */
    public abstract AbstractGraph<T, U> on(final Set<T> points);

    /**
     * Gets any Link connecting the arguments - if arguments do not override equals then Link is either unique or
     * does not exist
     *
     * @param linked1 (T)
     * @param linked2 (T)
     * @return (AbstractLink<T>) said link
     * @throws NoSuchElementException if linked1 and linked2 are not linked by any edge of the graph
     */
    public AbstractLink<T> linkOf(final T linked1, final T linked2) {
        return edgeSet.stream().filter(link -> link.getPoints().containsAll(Set.of(linked1, linked2)))
                .findAny().orElseThrow(() -> new NoSuchElementException("No edge connecting arguments."));
    }

    //TODO: Implement method
    public Path<T> isConnectedTo(final T t, final T u) {
        return new Path<>(List.of(t, u));
    }

    //TODO: doc
    public List<T> flow(final Function<Set<T>, T> chooser, final T point) {
        if (!hasNeighbors(point))
            return List.of(point);
        List<T> l = flow(chooser, chooser.apply(getNeighbors(point)));
        l.add(point);
        Collections.reverse(l);
        return l;
    }

    /**
     * @return (Set<U>) getter for immutable set of edges
     */
    public Set<U> getEdgeSet() {
        return edgeSet;
    }

    /**
     * @return (Set<T>) getter for immutable set of vertices
     */
    public Set<T> getPointSet() {
        return vertexSet;
    }

    /**
     * @return (T) gets any point in the set of vertices
     */
    public T getPoint() {
        return vertexSet.stream().findAny().orElseThrow(() -> new NoSuchElementException("Set of vertices is empty."));
    }

    /**
     * Checks whether given point is linked to other points
     *
     * @param point (T) given point
     * @return (boolean)
     * @throws NoSuchElementException if point is not in set of vertices
     */
    public boolean hasNeighbors(final T point) {
        if (vertexSet.contains(point)) {
            return edgeSet.stream().anyMatch(link -> link.getPoints().contains(point));
        }

        else throw new NoSuchElementException("Given point is not in set of vertices.");
    }

    /**
     * Gets the set of points linked to given point
     *
     * @param point (T) given point
     * @return (Set<T>) said set
     * @throws NoSuchElementException if point is not in set of vertices
     */
    public Set<T> getNeighbors(final T point) {
        if (vertexSet.contains(point))
            return edgeSet.stream()
                    .filter(l -> l.getPoints().contains(point))
                    .flatMap(l -> l.getPoints().stream())
                    .collect(Collectors.toCollection(HashSet::new));

        else throw new NoSuchElementException("Given point is not in set of vertices.");
    }
}
