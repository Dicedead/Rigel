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

    /**
     * A component is a maximally connected subset of a graph
     * @param point the point on which we want the compponent
     * @return the component onn which this point lies
     */
    public AbstractGraph<T, U> component(final T point) {
        return on(vertexSet.stream().map(v -> isConnectedTo(point, v)).flatMap(p -> p.getPointSet().stream())
                .collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     *
     * @return the Set of connected components of this graph
     */
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

    /**
     * If it exists, the edge between point t and u in this graph
     * @param t the point on which the edge should start
     * @param u the point on which the edge should end (order does not matter)
     * @return the edge on t and u if it exists in this graph
     */
    public U of(final T t, final T u)
    {
        return edgeSet.stream().filter(e -> e.getPoints().containsAll(Set.of(t, u))).findFirst().orElseThrow();
    }

    /**
     * If it exists, the path between point t and u in this graph
     * @param t the point on which the path should start
     * @param u the point on which the path should end
     * @return if it exists the path from t to u in this graph
     */
    //TODO: Implement method
    public Path<T> isConnectedTo(final T t, final T u) {
        return new Path<>(List.of(t, u));
    }
    /**
     *
     * @param v the vertex to test
     * @return Whether the graph contains this vertex or not
     */
    public boolean contains(final T v){return vertexSet.contains(v);}

    /**
     *
     * @param v the edge to test
     * @return Whether the graph contains this edge or not
     */
    public boolean containsEdge(final U v){return edgeSet.contains(v);}


    /**
     * Set theoric intersection of the vertex set
     * @param a the first graph to intersect
     * @param b the second one (here order does not matter)
     * @param <T> the type of object the graph holds
     * @return the set of points which are are both in a and b
     */
    public static <T> Set<T> intersectionVertex(AbstractGraph<T, ?> a, AbstractGraph<T, ?> b){
        return a.getPointSet().stream().filter(b::contains).collect(Collectors.toSet());
    }
    /**
     * Set theoric intersection of the edge set
     * @param a the first graph to intersect
     * @param b the second one (here order does not matter)
     * @param <T> the type of object the graph holds
     * @return the set of edges which are are both in a and b
     */
    public static <T, U extends UndirectedLink<T>> Set<U> intersectionEdges(AbstractGraph<T, U> a, AbstractGraph<T, U> b){
        return a.getEdgeSet().stream().filter(b::containsEdge).collect(Collectors.toSet());
    }

    /**
     * Graph theoric intersection
     * @param a the first graph to intersect
     * @param b the second one (here order does not matter)
     * @param <T> the type of object the graph holds
     * @param <U> its type of its links
     * @return the graph whose popints are both in a and b and whose edges are both in a and b
     */
    public static <T, U extends UndirectedLink<T>> AbstractGraph<T, U> intersection(AbstractGraph<T, U> a, AbstractGraph<T, U> b){
        return new AbstractGraph<T, U>(intersectionVertex(a, b),intersectionEdges(a, b));
    }


    /**
     * Graph theoric minus, the graph on the points not selected
     * @param points the points to remove
     * @return the graph obtained from this graph by removing the points passed
     */
    public AbstractGraph<T, U> minus(Set<T> points)
    {
        var c = new HashSet<T>(getPointSet());
        c.removeAll(points);
        return on(c);
    }

    /**
     * Intersects two graphs, take their intersection as a component and return the other newly created components
     * along with the intersection
     * @param a the first graph to "explode"
     * @param b the second one (here order does not matter)
     * @param <T> the type of object the graph holds
     * @param <U> its type of its links
     * @return the described structure dubbed "explosion product"
     */
    public static <T, U extends UndirectedLink<T>> Set<AbstractGraph<T, U>> eclat (AbstractGraph<T, U> a, AbstractGraph<T, U> b)
    {
        var I =intersection(a, b);
        var res = a.minus(I.getPointSet()).components();
        res.addAll(b.minus(I.getPointSet()).components());
        res.add(I);
        return res;

    }

    /**
     * Allows to navigate the graph by making a choice at each step
     * @param chooser the choice function
     * @param point the point to begin with
     * @return the List of points traversed by the choice function
     */
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
