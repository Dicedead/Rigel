package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import ch.epfl.rigel.math.sets.PartitionSet;
import javafx.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ConcreteGraph<T> extends MathSet<Pair<T, Link<T>>> implements Graph<T, PartitionSet<T>> {

    private final PartitionSet<T> vertices;
    private final MathSet<Link<T>> edges;

    public ConcreteGraph(PartitionSet<T> points, MathSet<Link<T>> edges, T t) {
        super(points.directSum(edges, t, new Link<T>(t, t)));
        vertices = points;
        this.edges = edges;

    }

    public ConcreteGraph(MathSet<T> points, MathSet<Link<T>> edges, T t) {
        super(points.directSum(edges, t, new Link<T>(t, t)));

        vertices = new PartitionSet<>(points, (T v, T u) -> findPathBetween(u, v).isPresent());
        this.edges = edges;

    }

    public ConcreteGraph(MathSet<Pair<T, Link<T>>> mathSet, T t) {
        super(mathSet);
        vertices = new PartitionSet<T>(suchThat(p -> p.getValue().equals(new Link<>(t, t)))
                .image(Pair::getKey), (T v, T u) -> findPathBetween(u, v).isPresent());
        this.edges = new MathSet<>(mathSet.image(Pair::getValue));

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

    //TODO
    @Override
    public Optional<Iterable<T>> findPathBetween(T v1, T v2) {
        return Optional.empty();
    }

    @Override
    public Graph<T, PartitionSet<T>> on(MathSet<T> points) {
        return new ConcreteGraph<>(suchThat(p -> points.contains(p.getKey())), stream()
                .filter(p -> p.getValue().getData().size() != 1).findFirst().orElseThrow().getKey());
    }

    @Override
    public Graph<T, PartitionSet<T>> connectedComponent(T point) {
        return on(new PartitionSet<T>(vertices.component(point)));
    }

    @Override
    public MathSet<Graph<T, PartitionSet<T>>> connectedComponents() {
        return vertexSet().components().stream().map(this::on).collect(MathSet.toSet());
    }

    public MathSet<Link<T>> edgeSet() {
        return edges;
    }

    public PartitionSet<T> vertexSet() {
        return vertices;
    }

}
