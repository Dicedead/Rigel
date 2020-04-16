package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedPair;
import ch.epfl.rigel.math.sets.PartitionSet;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class Path<T> extends OrderedPair<T> implements Graph<T, OrderedPair<T>> {


    public Path(OrderedPair<T> vertexes) {
        super(vertexes.toList());
    }


    @Override
    public Optional<OrderedPair<T>> getNeighbors(T point) {
        return Optional.of(new OrderedPair<>(prev(point), point, next(point)));
    }

    @Override
    public OrderedPair<T> flow(Function<OrderedPair<T>, T> chooser, T point) {
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
    public Graph<T, OrderedPair<T>> connectedComponent(T point) {
        return this;
    }

    @Override
    public MathSet<Graph<T, OrderedPair<T>>> connectedComponents() {
        return new MathSet<>(Collections.singleton(this));
    }

    @Override
    public MathSet<Link<T>> edgeSet() {
        return new MathSet<>( image(p -> new Link<>(p, next(p))));
    }

    @Override
    public OrderedPair<T> vertexSet() {
        return this;
    }
}
