package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Abstraction of a graph, a set of a set of edges and a set of vertices
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
abstract class Graph<T, U extends Link<T>> {

    private final Set<T> vertexSet;
    private final Set<U> edgeSet;

    /**
     * Main Graph constructor
     *
     * @param points (Set<T>) set of vertices
     * @param lines  (Set<U>) set of edges
     */
    public Graph(final Set<T> points, final Set<U> lines) {
        vertexSet = Set.copyOf(points);
        edgeSet = Set.copyOf(lines);
    }

    /**
     * Compositing Graph constructor, putting many graphs into one
     *
     * @param coll (List<Graph<T, U>>) collection of graphs to be united
     */
    public Graph(Collection<Graph<T, U>> coll) {
        vertexSet = Set.copyOf(coll.stream().flatMap(i -> i.getPointSet().stream()).collect(Collectors.toCollection(HashSet::new)));
        edgeSet = Set.copyOf(coll.stream().flatMap(i -> i.getEdgeSet().stream()).collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * A component is a maximally connected subset of a graph
     *
     * @param point the point on which we want the compponent
     * @return the component onn which this point lies
     */
    public Graph<T, U> component(final T point) {
        return on(vertexSet.stream().map(v -> findPathBetween(point, v)).flatMap(p -> p.getPointSet().stream())
                .collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * @return the Set of connected components of this graph
     */
    public Set<Graph<T, U>> components() {
        return vertexSet.stream().map(this::component).distinct().map(m -> this.on(m.getPointSet())).collect(Collectors.toSet());
    }

    /**
     * Creates a graph ON given set of vertices
     *
     * @param points (Set<T>)
     * @return (Graph < T, U >) some implementation of Graph<T,U>
     */
    public abstract Graph<T, U> on(Set<T> points);

    /**
     * Gets any Link connecting the arguments - if arguments do not override equals then Link is either unique or
     * does not exist
     *
     * @param linked1 (T)
     * @param linked2 (T)
     * @return (Link < T >) said link
     * @throws NoSuchElementException if linked1 and linked2 are not linked by any edge of the graph
     */
    public Link<T> linkOf(final T linked1, final T linked2) {
        return edgeSet.stream().filter(link -> link.getPoints().containsAll(Set.of(linked1, linked2)))
                .findAny().orElseThrow(() -> new NoSuchElementException("No edge connecting arguments."));
    }

    /**
     * If it exists, the edge between point value1 and value2 in this graph
     *
     * @param value1 (T) the point on which the edge should start
     * @param value2 (T) the point on which the edge should end (order does not matter)
     * @return (U) the edge on value1 and value2 if it exists in this graph
     */
    public U of(final T value1, final T value2) {
        return edgeSet.stream().filter(e -> e.getPoints().containsAll(Set.of(value1, value2))).findFirst().orElseThrow();
    }

    /**
     * Find a path between 2 parameter vertices of the graph
     *
     * @param v1 (T)
     * @param v2 (T)
     * @return (Path<T>) said path
     */
    public abstract Path<T> findPathBetween(final T v1, final T v2);

    /**
     * @param v (T) the vertex to test
     * @return (boolean) whether the graph contains this vertex or not
     */
    public boolean contains(final T v) {
        return vertexSet.contains(v);
    }

    /**
     * @param e (U) the edge to test
     * @return (boolean) whether the graph contains this edge or not
     */
    public boolean containsEdge(final U e) {
        return edgeSet.contains(e);
    }

    /**
     * UndirectedGraph theoric minus, the graph on the points not selected
     *
     * @param points the points to remove
     * @return the graph obtained from this graph by removing the points passed
     */
    public Graph<T, U> minus(Set<T> points) {
        final Set<T> c = new HashSet<>(getPointSet());
        c.removeAll(points);
        return on(c);
    }

    /**
     * Applies intersection operation on two graphs: this and otherGraph
     *
     * @param otherGraph (Graph<T, U>)
     * @return (Graph<T, U>) graph with intersected sets of edges and vertices
     */
    public abstract Graph<T, U> intersection(Graph<T, U> otherGraph);
    //Although this creates some similar code, it avoids really ugly casts and type checking

    /**
     * Intersects two graphs, take their intersection as a component and return the other newly created components
     * along with the intersection
     *
     * @param a   the first graph to "explode"
     * @param b   the second one (here order does not matter)
     * @param <V> the type of vertices the graph holds
     * @param <E> the type of the links
     * @return the described structure dubbed "explosion product"
     */
    public static <V, E extends UndirectedLink<V>> Set<Graph<V, E>> eclat(Graph<V, E> a, Graph<V, E> b) {
        final Graph<V, E> inter = a.intersection(b);
        final Set<Graph<V, E>> res = a.minus(inter.getPointSet()).components();
        res.addAll(b.minus(inter.getPointSet()).components());
        res.add(inter);
        return res;
    }

    /**
     * Allows to navigate the graph by making a choice at each step
     *
     * @param chooser the choice function
     * @param point   the point to begin with
     * @return the List of points traversed by the choice function
     */
    public List<T> flow(final Function<Set<T>, T> chooser, final T point) {
        if (!hasNeighbors(point))
            return List.of(point);
        final List<T> flowList = flow(chooser, chooser.apply(getNeighbors(point)));
        flowList.add(point);
        Collections.reverse(flowList);
        return flowList;
    }

    /**
     * @return (Set < U >) getter for immutable set of edges
     */
    public Set<U> getEdgeSet() {
        return edgeSet;
    }

    /**
     * @return (Set < T >) getter for immutable set of vertices
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
        } else throw new NoSuchElementException("Given point is not in set of vertices.");
    }

    /**
     * Gets the set of points linked to given point
     *
     * @param point (T) given point
     * @return (Set < T >) said set
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

    /**
     * Implementation of a directed graph: a Path from a root object to a final object
     *
     * @author Alexandre Sallinen (303162)
     * @author Salim Najib (310003)
     */
    public static final class Path<T> extends Graph<T, DirectedLink<T>> implements Iterable<T> {

        final private List<T> points;
        final private int length;

        /**
         * Main constructor
         *
         * @param points the list of point on wich to construct a path, the order being derived from the one in the list
         */
        public Path(List<T> points) {
            super(Set.copyOf(points), link(points));

            this.points = List.copyOf(points);
            this.length = points.size();
        }

        private static <T> Set<DirectedLink<T>> link(final List<T> points) {

            return IntStream.range(0, points.size() - 2)
                    .mapToObj(i -> new DirectedLink<T>(points.get(i), points.get(i + 1)))
                    .collect(Collectors.toSet());

        }

        /**
         * @return the path in the form of a list
         */
        public List<T> toList() {
            return points;
        }

        /**
         * Creates a Path from the root of this path to the first appearance of the parameter value
         * Path<T> is immutable iff T is immutable
         *
         * @param value (T)
         * @return (Path < T >) said Path
         * @throws IllegalArgumentException if value isn't in this path's vertices set
         */
        public Path<T> subpathTo(final T value) {
            Preconditions.checkArgument(points.contains(value));
            return new Path<>(points.subList(0, Collections.binarySearch(points, value,
                    Comparator.comparingInt(points::indexOf))));
        }

        /**
         * Creates a Path between given points as a subPath of this path, seeking for the edges connecting the given points
         * in this Path to filter out unconnected points
         *
         * @param points (Set<T>)
         * @return (Graph <T, DirectedLink <T>>) implemented as Path<T>
         */
        @Override
        public Graph<T, DirectedLink<T>> on(final Set<T> points) {
            if (this.points.containsAll(points))
                return new Path<>(IntStream.of(0, length)
                        .filter(i -> points.contains(this.points.get(i)) || points.contains(this.points.get(i - 1)))
                        .mapToObj(this.points::get).collect(Collectors.toList()));

            else throw new NoSuchElementException();
        }

        /**
         * @see Graph#findPathBetween(Object, Object)
         */
        @Override
        public Path<T> findPathBetween(final T v1, final T v2) {
            Preconditions.checkArgument(points.contains(v1) && points.contains(v2));
            return new Path<>(points.subList(points.indexOf(v1), points.indexOf(v2)));
        }

        /**
         * @see Graph#intersection(Graph)
         * @throws IllegalArgumentException if argument isn't a Path
         */
        @Override
        public Graph<T, DirectedLink<T>> intersection(Graph<T, DirectedLink<T>> otherGraph) {
            Preconditions.checkArgument(otherGraph.getClass() == Path.class);
            final List<T> otherList = ((Path<T>) otherGraph).points;
            otherList.retainAll(this.points);
            return new Path<T>(otherList);
        }

        /**
         * Creates a subpath of this path from value at index n to end
         *
         * @param n (int) start index of subpath
         * @return (Path < T >) said subpath
         * @throws IllegalArgumentException if n >= the size of this path
         */
        public Path<T> from(final int n) {
            Preconditions.checkArgument(n < length);
            return new Path<>(points.subList(n, length));
        }

        public T getPoint() {
            return head();
        }

        /**
         * @param n (int) index
         * @return (T) gets element at nth position in Path
         * @throws IllegalArgumentException if n is bigger than this' length
         */
        public T getAt(final int n) {
            Preconditions.checkArgument(n < length);
            return points.get(n);
        }

        /**
         * @return (T) gets first object in this path
         */
        public T head() {
            return points.get(0);
        }

        /**
         * @return (T) gets last object in this path
         */
        public T tail() {
            return points.get(length - 1);
        }

        /**
         * @return (int) gets path's length
         */
        public int getLength() {
            return length;
        }

        /**
         * @see List#stream()
         */
        public Stream<T> stream() {
            return points.stream();
        }

        /**
         * @see List#iterator()
         */
        @Override
        public Iterator<T> iterator() {
            return points.iterator();
        }

        /**
         * @see List#forEach(Consumer)
         */
        @Override
        public void forEach(Consumer<? super T> action) {
            points.forEach(action);
        }

        /**
         * @see List#spliterator()
         */
        @Override
        public Spliterator<T> spliterator() {
            return points.spliterator();
        }
    }
}
