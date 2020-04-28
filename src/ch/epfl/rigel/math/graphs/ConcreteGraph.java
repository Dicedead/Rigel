package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.*;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractPartitionSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.PartitionSet;

import java.util.*;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ConcreteGraph<T> extends MathSet<OptionalPair<T, Link<T>>> implements Graph<T, AbstractPartitionSet<T>> {

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
    public ConcreteGraph(AbstractMathSet<OptionalPair<T, Link<T>>> mathSet) {
        super(mathSet.getData());
        vertices = new PartitionSet<>(mathSet.image(p -> p.getKey().orElse(null)), (T v, T u) -> neighboursOf(of(u)).contains(v));
        this.edges = new MathSet<>(mathSet.image(p -> p.getValue().orElse(null)));

    }


    @Override
    public Optional<AbstractPartitionSet<T>> getNeighbours(final T point) {
        return Optional.of(new PartitionSet<>(edges.suchThat(l -> l.contains(point)).image(p -> p.next(point))));
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
