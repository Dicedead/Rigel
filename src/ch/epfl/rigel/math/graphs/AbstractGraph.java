package ch.epfl.rigel.math.graphs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
class AbstractGraph<T, U extends Arrow<T> > {

    private final Set<T> vertexSet;
    private final Set<U> edgeSet;
    public T getPoint()
    {
        return vertexSet.stream().findAny().orElseThrow();
    }

    public AbstractGraph<T, U> component(final T point) {
        return on(vertexSet.stream().map(v -> isConnectedTo(point, v)).flatMap(p -> p.getPointSet().stream()).collect(Collectors.toSet()));
    }
    public Set<AbstractGraph<T, U>> components() {
        return vertexSet.stream().map(this::component).distinct().map(m -> this.on(m.getPointSet())).collect(Collectors.toSet());
    }
    public AbstractGraph<T, U> on(Set<T> points) {
        if (vertexSet.containsAll(points))
            return new AbstractGraph<T, U>(points, edgeSet.stream().filter(l -> points.containsAll(l.getPoints())).collect(Collectors.toSet()));

        else throw new NoSuchElementException();
    }
    public U of(final T t, final T u)
    {
        return edgeSet.stream().filter(e -> e.getPoints().containsAll(Set.of(t, u))).findFirst().orElseThrow();
    }
    //TODO: Implement method
    public Path<T> isConnectedTo(final T t, final T u)
    {
        return new Path<>(List.of(t, u));
    }

    public boolean contains(final T v){return vertexSet.contains(v);}
    public boolean containsEdge(final U v){return edgeSet.contains(v);}

    public AbstractGraph(final Set<T> points,final Set<U> lines)
    {
        vertexSet = points;
        edgeSet = lines;
    }
    public static <T> Set<T> intersectionVertex(AbstractGraph<T, ?> a, AbstractGraph<T, ?> b){
        return a.getPointSet().stream().filter(b::contains).collect(Collectors.toSet());
    }
    public static <T, U extends Arrow<T>> Set<U> intersectionEdges(AbstractGraph<T, U> a, AbstractGraph<T, U> b){
        return a.getEdgeSet().stream().filter(b::containsEdge).collect(Collectors.toSet());
    }
    public static <T, U extends Arrow<T>> AbstractGraph<T, U> intersection(AbstractGraph<T, U> a, AbstractGraph<T, U> b){
        return new AbstractGraph<T, U>(intersectionVertex(a, b),intersectionEdges(a, b));
    }


    public AbstractGraph<T, U> minus(Set<T> points)
    {
        var c = new HashSet<T>(getPointSet());
        c.removeAll(points);
        return on(c);
    }

    public static <T, U extends Arrow<T>> Set<AbstractGraph<T, U>> eclat (AbstractGraph<T, U> a, AbstractGraph<T, U> b)
    {
        var I =intersection(a, b);
        var res = a.minus(I.getPointSet()).components();
        res.addAll(b.minus(I.getPointSet()).components());
        res.add(I);
        return res;

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
