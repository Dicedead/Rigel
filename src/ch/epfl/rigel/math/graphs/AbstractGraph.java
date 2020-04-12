package ch.epfl.rigel.math.graphs;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
abstract class AbstractGraph<T, U extends Arrow<T> > {
/*
    protected static <T> List<Node<T>> recursList(final Function<List<Node<T>>, Node<T>> chooser, Node<T> root)
    {
        final Node<T> selected = chooser.apply(root.getChildren());

        if (selected.isLeaf())
            return List.of(selected);

        List<Node<T>> prev = recursList(chooser, selected);
        prev.add(selected);
        return prev;
    }*/



    protected final Set<T> vertexSet;
    protected final Set<U> edgeSet;
    public T getPoint()
    {
        return vertexSet.stream().findAny().orElseThrow();
    }

    public AbstractGraph<T, U> component(final T point) {
        return on(vertexSet.stream().map(v -> isConnectedTo(point, v)).flatMap(p -> p.getPointSet().stream()).collect(Collectors.toSet()));
    }

    public abstract AbstractGraph<T, U> on(final Set<T> points);

    public U of(final T t, final T u)
    {
        return edgeSet.stream().filter(e -> e.getPoints().containsAll(Set.of(t, u))).findFirst().orElseThrow();
    }
    //TODO: Implement method
    public Path<T> isConnectedTo(final T t, final T u)
    {
        return new Path<>(List.of(t, u));
    }

    public AbstractGraph(final Set<T> points,final Set<U> lines)
    {
        vertexSet = points;
        edgeSet = lines;
    }

    public Set<U> getEdgeSet()
    {
        return edgeSet;
    }
    public Set<T> getPointSet()
    {
        return vertexSet;
    }
    public boolean hasNeighbors(final T point) {
        return getNeighbors(point).isEmpty();
    }

    public List<T> flow (final Function<Set<T>, T> chooser, final T point)
    {
        if (!hasNeighbors(point))
            return List.of(point);
        var l = flow(chooser, chooser.apply(getNeighbors(point)));
        l.add(point);
        Collections.reverse(l);
        return l;
    }

    public Set<T> getNeighbors(T point)
    {
        if (vertexSet.contains(point))
            return edgeSet.stream()
                    .filter(l -> l.getPoints().contains(point))
                    .flatMap(l -> l.getPoints().stream())
                    .collect(Collectors.toSet());

        else throw new NoSuchElementException();
    }

    public AbstractGraph(List<AbstractGraph<T, U> > l)
    {
        vertexSet = l.stream().flatMap(i -> i.getPointSet().stream()).collect(Collectors.toSet());
        edgeSet = l.stream().flatMap(i -> i.getEdgeSet().stream()).collect(Collectors.toSet());
    }



}
