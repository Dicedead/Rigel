package ch.epfl.rigel.math.graphs.poubelle;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.graphs.Link;
import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.PointedSet;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Abstraction of a graph, a set of a set of edges and a set of vertices
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
 class GraphComms<T> extends PointedSet<Pair<T, Link<T>>> implements AbstractGraph<T, MathSet<T>, MathSet<Link<T>>>
{


    /**
     * @return (MathSet <T>) getter for immutable set of vertices
     */

    @Override
    public MathSet<T> vertexSet()
    {
        return suchThat(p -> getSpecial().getValue() == p.getValue()).stream().map(Pair::getKey).collect(MathSet.toSet());
    }

    /**
     * @return (MathSet <U>) getter for immutable set of edges
     */
    @Override
    public MathSet<Link<T>> edgeSet()
    {
        return suchThat(p -> getSpecial().getKey() == p.getKey()).stream().map(Pair::getValue).collect(MathSet.toSet());
    }
    /**
     * Main Graph constructor
     *
     * @param points (Set<T>) set of vertices
     * @param lines  (Set<U>) set of edges
     */
    public GraphComms(final PointedSet<T> points, final PointedSet<Link<T>> lines) {
        super(points.directSum(lines));
    }

    /**
     * A component is a maximally connected subset of a graph
     *
     * @param point the point on which we want the compponent
     * @return the component onn which this point lies
     */
    @Override
    public GraphComms<T> component(final T point) {

        return on(suchThat(x -> findPathBetween(point, x.getKey()) != null).image(Pair::getKey));
        /*
        return on(stream().map(v -> findPathBetween(point, v)).flatMap(p -> p.getPointSet().stream())
                .collect(Collectors.toCollection(HashSet::new)));*/
    }

    /**
     * @return the Set of connected components of this graph
     */
    /**
     * Creates a graph ON given set of vertices
     *
     * @param points (Set<T>)
     * @return (Graph < T, U >) some implementation of Graph<T,U>
     */

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
        return edgeSet().suchThat(link -> link.getPoints().containsAll(Set.of(linked1, linked2)))
                .stream().findAny().orElseThrow(() -> new NoSuchElementException("No edge connecting arguments."));
    }

    /**
     * If it exists, the edge between point value1 and value2 in this graph
     *
     * @param value1 (T) the point on which the edge should start
     * @param value2 (T) the point on which the edge should end (order does not matter)
     * @return (U) the edge on value1 and value2 if it exists in this graph
     */
    public Link<T> of(final T value1, final T value2) {
        return edgeSet().suchThat(e -> e.getPoints().containsAll(Set.of(value1, value2))).stream().findFirst().orElseThrow();
    }

    @Override
    public AbstractGraph<T, MathSet<T>, MathSet<Link<T>>> on(AbstractGraph<T, MathSet<T>, MathSet<Link<T>>> points) {
        return suchThat(p -> points.contains(p.getKey()));
    }

    @Override
    public MathSet<AbstractGraph<T, MathSet<T>, MathSet<Link<T>>>> components() {
        return vertexSet().image(this::component).stream().distinct().map(m -> this.on(m.vertexSet())).collect(MathSet.toSet());
    }


    /**
     * Find a path between 2 parameter vertices of the graph
     *
     * @param v1 (T)
     * @param v2 (T)
     * @return (Path < T >) said path
     */
    /**
     * @param v (T) the vertex to test
     * @return (boolean) whether the graph contains this vertex or not
     */
    public boolean contains(final T v) {
        return vertexSet().in(v);
    }
    /**
     * @param e (U) the edge to test
     * @return (boolean) whether the graph contains this edge or not
     */

    /**
     * UndirectedGraph theoric minus, the graph on the points not selected
     *
     * @param points the points to remove
     * @return the graph obtained from this graph by removing the points passed
     */


    /**
     * Applies intersection operation on two graphs: this and otherGraph
     *
     * @param otherGraph (Graph<T, U, SV, SE>)
     * @return (Graph<T, U, SV, SE>) graph with intersected sets of edges and vertices
     */
    //Although this creates some similar code, it avoids really ugly casts and type checking

    /**
     * Applies intersection operation on two graphs: this and otherGraph
     *
     * @param otherGraph (Graph<T, U>)
     * @return (Graph<T, U>) graph with intersected sets of edges and vertices
     */
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
     *//*
    public static <V, E extends UndirectedLink<V>> MathSet<Graph<V, E>> eclat(Graph<V, E> a, Graph<V, E> b) {
        final Graph<V, E> inter = a.intersection(b);
        return inter.components()
                .union(a.minus(inter).components())
                .union(b.minus(inter).components());
    }*/

    /**
     * Allows to navigate the graph by making a choice at each step
     *
     * @param chooser the choice function
     * @param point   the point to begin with
     * @return the List of points traversed by the choice function
     */

    /**
     * Checks whether given point is linked to other points
     *
     * @param point (T) given point
     * @return (boolean)
     * @throws NoSuchElementException if point is not in set of vertices
     */
    public boolean hasNeighbors(final T point) {
        if (vertexSet().in(point)) {
            return edgeSet().suchThat(link -> link.getPoints().contains(point)).cardinality() != 0;
        } else throw new NoSuchElementException("Given point is not in set of vertices.");
    }

    @Override
    public MathSet<T> getNeighbors(T point) {
        if (vertexSet().in(point))
            return  edgeSet().suchThat(l -> l.getPoints().contains(point)).image(p -> p.next(point));

        else throw new NoSuchElementException("Given point is not in set of vertices.");
    }

    @Override
    public Iterable<T> flow(Function<MathSet<T>, T> chooser, T point) {

    }

    /**
     * Gets the set of points linked to given point
     *
     * @param point (T) given point
     * @return (Set <T>) said set
     * @throws NoSuchElementException if point is not in set of vertices
     */

    /**
     * Implementation of a directed graph: a Path from a root object to a final object.
     * A directed path can be seen as a sub-graph of any directed graph, making it a useful analysis tool.
     *
     * @author Alexandre Sallinen (303162)
     * @author Salim Najib (310003)
     */
    public static final class Path<T> extends GraphComms<T, DirectedLink<T>> {

        final private List<T> points;
        /**
         * Main Path constructor
         *
         * @param points (List<T>) the list of point on which to construct a path,
         *        the order being derived from the list's order
         */
        public Path(List<T> points) {
            super(new PointedSet<T>(points, points.get(0)), new PointedSet<>(link(points), new DirectedLink<>(points.get(0),points.get(0))));
            this.points = List.copyOf(points);
        }

        private static <T> Set<DirectedLink<T>> link(final List<T> points) {

            return IntStream.range(0, points.size() - 1)
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
         * @return (Path <T>) said Path
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
        public GraphComms<T, DirectedLink<T>> on(final Set<T> points) {
            if (this.points.containsAll(points))
                return new Path<>(IntStream.of(0, cardinality())
                        .filter(i -> points.contains(this.points.get(i)) || points.contains(this.points.get(i - 1)))
                        .mapToObj(this.points::get).collect(Collectors.toList()));

            else throw new NoSuchElementException();
        }

        @Override
        public GraphComms<T, DirectedLink<T>> on(MathSet<T> points) {
            return null;
        }

        @Override
        public GraphComms<T, DirectedLink<T>> on(GraphComms<T, DirectedLink<T>> points) {
            return null;
        }

        /**
         * @see GraphComms#findPathBetween(Object, Object)
         */
        @Override
        public Path<T> findPathBetween(final T v1, final T v2) {
            Preconditions.checkArgument(points.contains(v1) && points.contains(v2));
            return new Path<>(points.subList(points.indexOf(v1), points.indexOf(v2)));
        }

        /**
         * @see GraphComms#intersection(GraphComms)
         * @throws IllegalArgumentException if argument isn't a Path
         */
        @Override
        public GraphComms<T, DirectedLink<T>> intersection(GraphComms<T, DirectedLink<T>> otherGraph) {
            Preconditions.checkArgument(otherGraph.getClass() == Path.class);
            final List<T> otherList = ((Path<T>) otherGraph).points;
            otherList.retainAll(this.points);
            return new Path<>(otherList);
        }

        /**
         * Creates a sub-path of this path from value at index n to end
         *
         * @param n (int) start index of sub-path
         * @return (Path < T >) said sub-path
         * @throws IllegalArgumentException if n >= the size of this path
         */
        public Path<T> from(final int n) {
            Preconditions.checkArgument(n < cardinality());
            return new Path<>(points.subList(n, cardinality()));
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
            Preconditions.checkArgument(n < cardinality());
            return points.get(n);
        }
        /**
         * Creates a path beginning at this path end's ending at the beginning of this one
         *
         * @return (Path<T>) a reversed path
         */
        public Path<T> inverse () {
            final var a = new ArrayList<>(toList());
            Collections.reverse(a);
            return new Path<>(a);
        }
        /**
         * Creates a path begining at this path and ending at the end of the other
         * @param otherPath (Path<T>) the path to append
         * @return (Path<T>) a path composed of an appending of the two paths
         */
        public Path<T> add (Path<T> otherPath) {
            final var secondHalf = otherPath.toList();
            final var firstHalf = new ArrayList<>(toList());

            firstHalf.addAll(secondHalf);
            return new Path<>(firstHalf);
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
            return points.get(cardinality() - 1);
        }
    }
}
