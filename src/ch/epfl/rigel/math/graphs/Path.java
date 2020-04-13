package ch.epfl.rigel.math.graphs;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Path<T> extends AbstractGraph<T, DirectedLink<T>>{

    final private List<T> points;
    final private int length;

    public Path(List<T> points) {
        super(Set.copyOf(points), link(points));

        this.points = List.copyOf(points);
        this.length = points.size();
    }


    private static <T> Set<DirectedLink<T>> link(final List<T> points)
    {

        return IntStream.of(0, points.size())
                .mapToObj(i -> new DirectedLink<T>(points.get(i), points.get(i + 1)))
                .collect(Collectors.toSet());

    }
    public List<T> toList()
    {
        return points;
    }

    @Override
    public T getPoint() {
        return points.get(0);
    }

    @Override
    public AbstractGraph<T, DirectedLink<T>> on(final Set<T> points) {
        if (this.points.containsAll(points))
            return new Path<>(IntStream.of(0, length)
                    .filter(i -> points.contains(this.points.get(i)) || points.contains(this.points.get(i - 1)))
                    .mapToObj(this.points::get).collect(Collectors.toList()));

        else throw new NoSuchElementException();
    }

    @Override
    public Path<T> isConnectedTo(final T t, final T u) {
        return new Path<>(points.subList(points.indexOf(t), points.indexOf(u)));
    }

    public Stream<T> stream ()
    {
        return points.stream();
    }
    public Path<T> getO(final T t)
    {
        return new Path<>(points.subList(0, points.lastIndexOf(t)));
    }
    public T getN(final int n)
    {
        return points.get(n);
    }

    public Path<T> from(final int n)
    {
        return new Path<>(points.subList(n, length));
    }
    public T head(){return points.get(0);}
    public T tail(){return points.get(length);}
    public int getLength() {
        return length;
    }
}
