package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.*;
import com.sun.javafx.geom.Edge;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ConcreteGraph<T> extends MathSet<Maybe<T, Link<T>>> implements Graph<T, PartitionSet<T>> {

    private final PartitionSet<T> vertices;
    private final MathSet<Link<T>> edges;

    public ConcreteGraph(PartitionSet<T> points, MathSet<Link<T>> edges) {
        super(points.directSum(edges));
        vertices = points;
        this.edges = edges;

    }

    public ConcreteGraph(MathSet<T> points, MathSet<Link<T>> edges) {
        super(points.directSum(edges));

        vertices = new PartitionSet<>(points, this::areConnected);
        this.edges = edges;

    }

    public ConcreteGraph(MathSet<Maybe<T, Link<T>>> mathSet, T t) {
        super(mathSet.getData());
        vertices = new PartitionSet<T>(mathSet.image(p -> p.getKey().orElse(null)), (T v, T u) -> rec(of(u)).contains(v));
        this.edges = new MathSet<>(mathSet.image(p -> p.getValue().orElse(null)));

    }

    public Optional<PartitionSet<T>> getNeighbours(final T point) {
        return Optional.of(new PartitionSet<>(edges.suchThat(l -> l.contains(point)).image(p -> p.next(point))));
    }

    @Override
    public OrderedSet<T> flow(final Function<PartitionSet<T>, T> chooser, final T point) {
        if (getNeighbours(point).isEmpty())
            return new OrderedSet<>(point);
        final List<T> flowList = flow(chooser, chooser.apply(getNeighbours(point).get())).toList();
        flowList.add(point);
        Collections.reverse(flowList);
        return new OrderedSet<>(flowList);
    }


    private MathSet<T> rec(MathSet<T> t)
    {
        SetFunction<T, Optional<PartitionSet<T>>> f = this::getNeighbours;

        var a = f.andThen(Optional::isPresent).preImageOf(true).solveIn(t);
        if (a.cardinality() == 0)
            return of();

        var b = a.image(f.andThen(Optional::get)).stream().collect(MathSet.union());
        return a.union(rec(b));
    }

    @Override
    public Graph<T, PartitionSet<T>> on(MathSet<T> points) {
        return new ConcreteGraph<T>(vertices.intersection(points), edges.suchThat(points::containsSet));
    }

    @Override
    public Graph<T, PartitionSet<T>> connectedComponent(T point) {
        return on(new PartitionSet<T>(vertices.component(point)));
    }

    @Override
    public MathSet<Graph<T, PartitionSet<T>>> connectedComponents() {
        return vertexSet().components().image(this::on);
    }

    public MathSet<Link<T>> edgeSet() {
        return edges;
    }

    public PartitionSet<T> vertexSet() {
        return vertices;
    }

}
