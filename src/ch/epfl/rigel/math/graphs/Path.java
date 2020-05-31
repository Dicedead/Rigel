package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.abstraction.AbstractPartitionSet;
import ch.epfl.rigel.math.sets.implement.MathSet;
import ch.epfl.rigel.math.sets.implement.OrderedTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Connected and directed recursively defined graph where every element has either 0 or 1 child
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Path<T> extends OrderedTuple<T> implements Graph<T, Path<T>> {

    public Path(AbstractOrderedTuple<T> vertices) {
        super(vertices.toList());
    }

    /**
     * Main Path constructor
     *
     * @param vertices (Iterable<T>) iterable of points on which to construct a path,
     *                               the order being derived from the iterable's order
     */
    public Path(Iterable<T> vertices) {
        super(vertices);
    }

    /**
     * Alternate Path constructor from array (or ellipse)
     *
     * @param vertices (T...)
     */
    @SafeVarargs
    public Path(T... vertices) {
        super(vertices);
    }

    /**
     * Construct a path of GraphNodes from a string
     * Example: String "Bob" is mapped onto B -> o -> b, where in x -> y, x is the parent of y
     *
     * @param s (String) given string
     * @return (Path<GraphNode<Character>>)
     */
    public static Path<GraphNode<Character>> fromString(String s) {
        return fromStringWithRoot(s, new GraphNode<>(s.charAt(0)), 1);
    }

    /**
     * Construct a path of GraphNodes from a string + a given root for the node hierarchy thus created
     * Example: String "lice" and root "A" are thus mapped onto A -> l -> i -> c -> e, where in x -> y, x is the parent
     * of y
     *
     * @param string (String) string to attach to the root
     * @param root (GraphNode<Character>) given character root
     * @param startPos (int)
     * @return (Path<GraphNode<Character>>) said path
     */
    public static Path<GraphNode<Character>> fromStringWithRoot(String string, GraphNode<Character> root, int startPos) {
        List<GraphNode<Character>> data = new ArrayList<>();
        data.add(root);
        for (int i = startPos; i < string.length(); i++) {
            data.add(new GraphNode<>(string.charAt(i), data.get(i - 1)));
        }
        return new Path<>(data);
    }

    /**
     * Get given point and the point before and after given point if they exist in this path
     *
     * @param point (T) given point
     * @return (Optional<Path<T>>) a path of said association
     */
    @Override
    public Optional<Path<T>> getNeighbours(T point) {
        return Optional.of(new Path<>(prev(point), point, next(point)));
    }

    /**
     * @return default flow corresponding to identity function: Path is already indexed
     */
    public OrderedTuple<T> flow() {
        return this;
    }

    /**
     * Creates a Path from v1 to v2
     * Path<T> is immutable iff T is immutable
     *
     * @param v1 (T)
     * @param v2 (T)
     * @return (Path < T >) said Path
     * @throws IllegalArgumentException if value isn't in this path's vertices set
     */
    public Path<T> subPath(T v1, T v2) {
        return new Path<>(toList().subList(Integer.min(indexOf(v1), indexOf(v2)), Integer.max(indexOf(v1), indexOf(v2))));
    }

    /**
     * Creates an OrderedTuple from v1 to v2
     * OrderedTuple<T> is immutable iff T is immutable
     *
     * @param v1 (T)
     * @param v2 (T)
     * @return (OrderedTuple < T >) said tuple
     * @throws IllegalArgumentException if value isn't in this path's vertices set
     */
    public Optional<AbstractOrderedTuple<T>> findPathBetween(T v1, T v2) {
        if (!(contains(v1) && contains(v2)))
            return Optional.empty();
        return Optional.of(subPath(v1, v2));
    }

    @Override
    public Graph<T, AbstractPartitionSet<T>> on(AbstractMathSet<T> points) {
        return new ConcreteGraph<>(this, edgeSet()).on(points);
    }

    /**
     * @see Graph#connectedComponent(Object)
     * By definition, a path is connected: this method returns this.
     */
    @Override
    public Graph<T, Path<T>> connectedComponent(T point) {
        return this;
    }

    /**
     * @see Graph#connectedComponents()
     * By definition, a path is connected: this method returns this wrapped in a MathSet.
     */
    @Override
    public MathSet<Graph<T, Path<T>>> connectedComponents() {
        return MathSet.of(this);
    }

    /**
     * @see Graph#edgeSet()
     */
    @Override
    public MathSet<Link<T>> edgeSet() {
        return image(p -> new Link<>(p, next(p)));
    }

    /**
     * @see Graph#vertexSet()
     *
     * @return this, as by recursive definition, a path is its own vertex set
     */
    @Override
    public Path<T> vertexSet() {
        return this;
    }

    /**
     * Creates a path beginning at this path end's ending at the beginning of this one
     *
     * @return (Path < T >) a reversed path
     */
    public Path<T> reverse() {
        final List<T> copy = new ArrayList<>(toList());
        Collections.reverse(copy);
        return new Path<>(copy);
    }

    /**
     * Creates a path beginning at this path and ending at the end of the other
     *
     * @param otherPath (Path<T>) the path to append
     * @return (Path < T >) a path composed of an appending of the two paths
     */
    public Path<T> add(Path<T> otherPath) {
        final var secondHalf = otherPath.toList();
        final var firstHalf = new ArrayList<>(toList());

        firstHalf.addAll(secondHalf);
        return new Path<>(firstHalf);
    }
}
