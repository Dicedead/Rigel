package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.abtract.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.abtract.AbstractPartitionSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.OrderedTuple;
import ch.epfl.rigel.math.sets.abtract.SetFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Path<T> extends OrderedTuple<T> implements Graph<T, Path<T>> {


    public Path(OrderedTuple<T> vertices) {
        super(vertices.toList());
    }

    /**
     * Main Path constructor
     *
     * @param vertices (List<T>) the list of point on which to construct a path,
     *        the order being derived from the list's order
     */
    public Path(List<T> vertices) {
        super(vertices);
    }

    public Path(Iterable<T> vertices) {
        super(vertices);
    }

    @SafeVarargs
    public Path(T... vertices) {
        super(vertices);
    }

    @Override
    public Optional<Path<T>> getNeighbours(T point) {
        return Optional.of(new Path<>(prev(point), point, next(point)));
    }

    @Override
    public OrderedTuple<T> flow(SetFunction<Path<T>, T> chooser, T point) {
        return this;
    }

    /**
     *
     * @return default flow corresponding to identity function
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
     * @return (Path <T>) said Path
     * @throws IllegalArgumentException if value isn't in this path's vertices set
     */
    public Path<T> subPath(T v1, T v2)
    {
        return new Path<>(toList().subList(Integer.min(indexOf(v1), indexOf(v2)), Integer.max(indexOf(v1), indexOf(v2))));
    }

    /**
     * Creates an OrderedTuple from v1 to v2
     * OrderedTuple<T> is immutable iff T is immutable
     *
     * @param v1 (T)
     * @param v2 (T)
     * @return (OrderedTuple <T>) said tuple
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

    @Override
    public Graph<T, Path<T>> connectedComponent(T point) {
        return this;
    }

    @Override
    public MathSet<Graph<T, Path<T>>> connectedComponents() {
        return of(this);
    }

    @Override
    public MathSet<Link<T>> edgeSet() {
        return image(p -> new Link<>(p, next(p)));
    }

    @Override
    public Path<T> vertexSet() {
        return this;
    }

    /**
     * Creates a path beginning at this path end's ending at the beginning of this one
     *
     * @return (Path<T>) a reversed path
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