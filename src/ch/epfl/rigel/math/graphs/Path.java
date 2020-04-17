package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import ch.epfl.rigel.math.sets.PartitionSet;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public final class Path<T> extends OrderedSet<T> implements Graph<T, OrderedSet<T>> {


    public Path(OrderedSet<T> vertices) {
        super(vertices.toList());
    }


    @Override
    public Optional<OrderedSet<T>> getNeighbors(T point) {
        return Optional.of(new OrderedSet<>(prev(point), point, next(point)));
    }

    @Override
    public OrderedSet<T> flow(Function<OrderedSet<T>, T> chooser, T point) {
        return this;
    }

    @Override
    public Optional<Iterable<T>> findPathBetween(T v1, T v2) {
        if (! in(v1) || ! in(v2))
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
        return new MathSet<>( image(p -> new Link<>(p, next(p))));
    }

    @Override
    public OrderedSet<T> vertexSet() {
        return this;
    }
}
