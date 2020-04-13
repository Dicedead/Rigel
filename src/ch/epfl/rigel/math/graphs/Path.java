package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implementation of a directed graph: a Path from a root object to a final object
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Path<T> extends AbstractGraph<T, DirectedLink<T>> implements Iterable<T> {

    final private List<T> points;
    final private int length;

    /**
     * Path Constructor: takes as parameter a list of vertices and connects them mono-directionally from start to
     * finish:
     * 0 -> 1 -> ... -> points.size()-1
     * Path<T> is immutable iff T is immutable
     *
     * @param points (List<T>) list of vertices to join
     */
    public Path(List<T> points) {
        super(Set.copyOf(points), link(points));

        this.points = List.copyOf(points);
        this.length = points.size();
    }

    /**
     * Creates a Path from the root of this path to the last appearance of the parameter value
     * Path<T> is immutable iff T is immutable
     *
     * @param value (T)
     * @return (Path<T>) said Path
     * @throws IllegalArgumentException if value isn't in this path's vertices set
     */
    public Path<T> subpathTo(final T value) {
        Preconditions.checkArgument(points.contains(value));
        return new Path<>(points.subList(0, points.lastIndexOf(value)));
    }

    /**
     * Creates a Path between given points as a subPath of this path, seeking for the edges connecting the given points
     * in this Path to filter out unconnected points
     *
     * @param argPoints (Set<T>)
     * @return (AbstractGraph<T, DirectedLink<T>>) implemented as Path<T>
     */
    @Override
    public AbstractGraph<T, DirectedLink<T>> on(final Set<T> argPoints) {
        if (points.containsAll(argPoints))
            return new Path<>(IntStream.range(0, length - 1)
                    .filter(i -> argPoints.contains(points.get(i)) || argPoints.contains(points.get(i - 1)))
                    .mapToObj(points::get)
                    .collect(Collectors.toCollection(ArrayList::new)));
        else throw new NoSuchElementException();
    }

    //TODO: doc
    @Override
    public Path<T> isConnectedTo(final T t, final T u) {
        return new Path<>(points.subList(points.indexOf(t), points.indexOf(u)));
    }

    /**
     * Creates a subpath of this path from value at index n to end
     *
     * @param n (int) start index of subpath
     * @return (Path<T>) said subpath
     * @throws IllegalArgumentException if n >= the size of this path
     */
    public Path<T> from(final int n) {
        Preconditions.checkArgument(n < length);
        return new Path<>(points.subList(n, length));
    }

    public T getPoint() {
        return head();
    }

    /**
     * @param n (int) index
     * @return (T) gets element at nth position in Path
     * @throws IllegalArgumentException if n is bigger than this' length
     */
    public T getAt(final int n) {
        Preconditions.checkArgument(n < length);
        return points.get(n);
    }

    /**
     * @return (T) gets first object in this path
     */
    public T head() {
        return points.get(0);
    }

    /**
     * @return (T) gets last object in this path
     */
    public T tail() {
        return points.get(length - 1);
    }

    /**
     * @return (int) gets path's length
     */
    public int getLength() {
        return length;
    }

    private static <T> Set<DirectedLink<T>> link(final List<T> points) {
        return IntStream.range(0, points.size() - 1)
                .mapToObj(i -> new DirectedLink<T>(points.get(i), points.get(i + 1)))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * @see List#stream()
     */
    public Stream<T> stream() {
        return points.stream();
    }

    /**
     * @see List#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return points.iterator();
    }

    /**
     * @see List#forEach(Consumer)
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        points.forEach(action);
    }

    /**
     * @see List#spliterator()
     */
    @Override
    public Spliterator<T> spliterator() {
        return points.spliterator();
    }
}
