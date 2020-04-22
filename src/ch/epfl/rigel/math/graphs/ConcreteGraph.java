package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.*;

import java.util.*;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ConcreteGraph<T> extends MathSet<Maybe<T, Link<T>>> implements Graph<T, PartitionSet<T>> {

    private final PartitionSet<T> vertices;
    private final MathSet<Link<T>> edges;

    /**
     * Constructing a graph from points and edges, each partition is a connected component
     * @param points the points
     * @param edges the edges
     */
    public ConcreteGraph(PartitionSet<T> points, MathSet<Link<T>> edges) {
        super(points.directSum(edges));
        vertices = points;
        this.edges = edges;

    }

    /**
     * Constructing a graph from points and edges
     * @param points the points
     * @param edges the edges
     */
    public ConcreteGraph(MathSet<T> points, MathSet<Link<T>> edges) {
        super(points.directSum(edges));

        vertices = new PartitionSet<>(points, this::areConnected);
        this.edges = edges;

    }
    /**
     * Constructing a graph from points and edges, condensed in a single MathSet
     * @param mathSet the underlying data
     */
    public ConcreteGraph(MathSet<Maybe<T, Link<T>>> mathSet) {
        super(mathSet.getData());
        vertices = new PartitionSet<>(mathSet.image(p -> p.getKey().orElse(null)), (T v, T u) -> rec(of(u)).contains(v));
        this.edges = new MathSet<>(mathSet.image(p -> p.getValue().orElse(null)));

    }


    @Override
    public Optional<PartitionSet<T>> getNeighbours(final T point) {
        return Optional.of(new PartitionSet<>(edges.suchThat(l -> l.contains(point)).image(p -> p.next(point))));
    }

    @Override
    public OrderedTuple<T> flow(final SetFunction<PartitionSet<T>, T> chooser, final T point) {
        if (getNeighbours(point).isEmpty())
            return new OrderedTuple<>(point);
        final List<T> flowList = flow(chooser, chooser.apply(getNeighbours(point).get())).toList();
        flowList.add(point);
        Collections.reverse(flowList);
        return new OrderedTuple<>(flowList);
    }


    private MathSet<T> rec(MathSet<T> t)
    {
        SetFunction<T, Optional<PartitionSet<T>>> f = this::getNeighbours;

        var a = f.andThen(Optional::isPresent).preImageOf(true).solveIn(t);
        if (a.cardinality() == 0)
            return of();

        var b = unionOf(a.image(f.andThen(Optional::get)));
        return a.union(rec(b));
    }

    @Override
    public Graph<T, PartitionSet<T>> on(MathSet<T> points) {
        return new ConcreteGraph<>(vertices.intersection(points), edges.suchThat(points::containsSet));
    }

    @Override
    public Graph<T, PartitionSet<T>> connectedComponent(T point) {
        return on(new PartitionSet<>(vertices.component(point)));
    }

    @Override
    public MathSet<Graph<T, PartitionSet<T>>> connectedComponents() {
        return vertexSet().components().image(this::on);
    }


    @Override
    public MathSet<Link<T>> edgeSet() {
        return edges;
    }

    @Override
    public PartitionSet<T> vertexSet() {
        return vertices;
    }

}
