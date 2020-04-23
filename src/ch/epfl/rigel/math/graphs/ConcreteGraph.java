package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.*;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.abtract.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.abtract.AbstractPartitionSet;
import ch.epfl.rigel.math.sets.abtract.SetFunction;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.OrderedTuple;
import ch.epfl.rigel.math.sets.concrete.PartitionSet;

import java.util.*;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ConcreteGraph<T> extends MathSet<Maybe<T, Link<T>>> implements Graph<T, AbstractPartitionSet<T>> {

    private final AbstractPartitionSet<T> vertices;
    private final AbstractMathSet<Link<T>> edges;

    /**
     * Constructing a graph from points and edges, each partition is a connected component
     * @param points the points
     * @param edges the edges
     */
    public ConcreteGraph(AbstractPartitionSet<T> points, AbstractMathSet<Link<T>> edges) {
        super(points.directSum(edges));
        vertices = points;
        this.edges = edges;

    }

    /**
     * Constructing a graph from points and edges
     * @param points the points
     * @param edges the edges
     */
    public ConcreteGraph(AbstractMathSet<T> points, AbstractMathSet<Link<T>> edges) {
        super(points.directSum(edges));

        vertices = new PartitionSet<>(points, this::areConnected);
        this.edges = edges;

    }
    /**
     * Constructing a graph from points and edges, condensed in a single MathSet
     * @param mathSet the underlying data
     */
    public ConcreteGraph(AbstractMathSet<Maybe<T, Link<T>>> mathSet) {
        super(mathSet.getData());
        vertices = new PartitionSet<>(mathSet.image(p -> p.getKey().orElse(null)), (T v, T u) -> rec(of(u)).contains(v));
        this.edges = new MathSet<>(mathSet.image(p -> p.getValue().orElse(null)));

    }


    @Override
    public Optional<AbstractPartitionSet<T>> getNeighbours(final T point) {
        return Optional.of(new PartitionSet<>(edges.suchThat(l -> l.contains(point)).image(p -> p.next(point))));
    }

    @Override
    public AbstractOrderedTuple<T> flow(final SetFunction<AbstractPartitionSet<T>, T> chooser, final T point) {
        if (getNeighbours(point).isEmpty())
            return new OrderedTuple<>(point);
        final List<T> flowList = flow(chooser, chooser.apply(getNeighbours(point).get())).toList();
        flowList.add(point);
        Collections.reverse(flowList);
        return new OrderedTuple<>(flowList);
    }


    private AbstractMathSet<T> rec(AbstractMathSet<T> t)
    {
        SetFunction<T, Optional<AbstractPartitionSet<T>>> f = this::getNeighbours;

        var a = f.andThen(Optional::isPresent).preImageOf(true).solveIn(t);
        if (a.cardinality() == 0)
            return of();

        var b = AbstractMathSet.unionOf(a.image(f.andThen(Optional::get)));
        return a.union(rec(b));
    }

    @Override
    public Graph<T, AbstractPartitionSet<T>> on(AbstractMathSet<T> points) {
        return new ConcreteGraph<>(vertices.intersection(points), edges.suchThat(points::containsSet));
    }

    @Override
    public Graph<T, AbstractPartitionSet<T>> connectedComponent(T point) {
        return on(new PartitionSet<>(vertices.component(point)));
    }

    @Override
    public AbstractMathSet<Graph<T, AbstractPartitionSet<T>>> connectedComponents() {
        return vertexSet().components().image(this::on);
    }


    @Override
    public AbstractMathSet<Link<T>> edgeSet() {
        return edges;
    }

    @Override
    public AbstractPartitionSet<T> vertexSet() {
        return vertices;
    }

}
