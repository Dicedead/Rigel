package ch.epfl.rigel.math.graphs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstraction of a graph, a set of a set of edges and a set of vertices
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
class AbstractGraph<T, U extends AbstractLink<T>> {

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
    public Set<AbstractGraph<T, U>> components() {
        return vertexSet.stream().map(this::component).distinct().map(m -> this.on(m.getPointSet())).collect(Collectors.toSet());
    }

    /**
     * Creates a graph ON given set of vertices
     *
     * @param points (Set<T>)
     * @return (AbstractGraph<T, U>) some implementation of AbstractGraph<T,U>
     */
    public AbstractGraph<T, U> on(Set<T> points) {
        if (vertexSet.containsAll(points))
            return new AbstractGraph<T, U>(points, edgeSet.stream().filter(l -> points.containsAll(l.getPoints())).collect(Collectors.toSet()));
        else return null;
    }

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
    public U of(final T t, final T u)
    {
        return edgeSet.stream().filter(e -> e.getPoints().containsAll(Set.of(t, u))).findFirst().orElseThrow();
    }
    //TODO: Implement method
    public Path<T> isConnectedTo(final T t, final T u) {
        return new Path<>(List.of(t, u));
    }

    public boolean contains(final T v){return vertexSet.contains(v);}
    public boolean containsEdge(final U v){return edgeSet.contains(v);}


    public static <T> Set<T> intersectionVertex(AbstractGraph<T, ?> a, AbstractGraph<T, ?> b){
        return a.getPointSet().stream().filter(b::contains).collect(Collectors.toSet());
    }
    public static <T, U extends UndirectedLink<T>> Set<U> intersectionEdges(AbstractGraph<T, U> a, AbstractGraph<T, U> b){
        return a.getEdgeSet().stream().filter(b::containsEdge).collect(Collectors.toSet());
    }
    public static <T, U extends UndirectedLink<T>> AbstractGraph<T, U> intersection(AbstractGraph<T, U> a, AbstractGraph<T, U> b){
        return new AbstractGraph<T, U>(intersectionVertex(a, b),intersectionEdges(a, b));
    }


    public AbstractGraph<T, U> minus(Set<T> points)
    {
        var c = new HashSet<T>(getPointSet());
        c.removeAll(points);
        return on(c);
    }

    public static <T, U extends UndirectedLink<T>> Set<AbstractGraph<T, U>> eclat (AbstractGraph<T, U> a, AbstractGraph<T, U> b)
    {
        var I =intersection(a, b);
        var res = a.minus(I.getPointSet()).components();
        res.addAll(b.minus(I.getPointSet()).components());
        res.add(I);
        return res;

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
