package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import ch.epfl.rigel.math.sets.PartitionSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Path<T> extends OrderedSet<T> implements Graph<T, OrderedSet<T>> {


    public Path(OrderedSet<T> vertices) {
        super(vertices.toList());
    }

    public Path(List<T> vertices) {
        super(vertices);
    }

    public Path(Iterable<T> vertices) {
        super(vertices);
    }


    @Override
    public Optional<OrderedSet<T>> getNeighbours(T point) {
        return Optional.of(new OrderedSet<>(prev(point), point, next(point)));
    }

    @Override
    public OrderedSet<T> flow(Function<OrderedSet<T>, T> chooser, T point) {
        return this;
    }

    public OrderedSet<T> flow() {
        return this;
    }

    @Override
    public Optional<Iterable<T>> findPathBetween(T v1, T v2) {
        if (!(contains(v1) && contains(v2)))
            return Optional.empty();
        return Optional.of(toList().subList(indexOf(v1), indexOf(v2)));
    }

    @Override
    public Graph<T, PartitionSet<T>> on(MathSet<T> points) {
        return new ConcreteGraph<T>(this, edgeSet(), at(0)).on(points);
    }

    @Override
    public Graph<T, OrderedSet<T>> connectedComponent(T point) {
        return this;
    }

    @Override
    public MathSet<Graph<T, OrderedSet<T>>> connectedComponents() {
        return new MathSet<>(Collections.singleton(this));
    }

    @Override
    public MathSet<Link<T>> edgeSet() {
        return new MathSet<>(image(p -> new Link<>(p, next(p))));
    }

    @Override
    public OrderedSet<T> vertexSet() {
        return this;
    }

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
