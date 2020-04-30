package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.implement.IndexedSet;
import ch.epfl.rigel.math.sets.implement.MathSet;
import ch.epfl.rigel.math.sets.implement.OrderedTuple;

import java.util.List;
import java.util.Optional;

public final class Cycle<T> extends OrderedTuple<T> implements Graph<T, AbstractOrderedTuple<T>> {

    private final T start;
    private final T end;

    public Cycle(List<T> listedData) {
        super(listedData);
        start = listedData.get(0);
        end = listedData.get(listedData.size() - 1);
    }

    public Cycle(OrderedTuple<T> orderedData) {
        this(orderedData.toList());
    }

    /**
     * Gets the set of points linked to given point
     *
     * @param point (T) given point
     * @return (Set < T >) said set
     */
    @Override
    public Optional<AbstractOrderedTuple<T>> getNeighbours(T point) {
        return Optional.of(new OrderedTuple<>(prev(point), next(point)));
    }

    /**
     * Creates a graph ON given set of vertices
     *
     * @param points (Set<T>)
     * @return (Graph < T, U >) some implementation of Graph<T,U>
     */
    @Override
    public Graph<T, ? extends AbstractMathSet<T>> on(AbstractMathSet<T> points) {
        return new ConcreteGraph<T>(this, edgeSet()).on(points);
    }

    /**
     * A component is a maximally connected subset of a graph
     *
     * @param point the point on which we want the compponent
     * @return the component onn which this point lies
     */
    @Override
    public Graph<T, AbstractOrderedTuple<T>> connectedComponent(T point) {
        return this;
    }

    /**
     * @return (AbstractMathSet<Graph<T, AbstractOrderedTuple<T>>>) the Set of connected components of this graph
     */
    @Override
    public AbstractMathSet<Graph<T, AbstractOrderedTuple<T>>> connectedComponents() {
        return MathSet.of(this);
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    public boolean isLastBeforeRepeat(T value) {
        return value == end;
    }

    /**
     * @return (AbstractMathSet<Link<T>>) getter for immutable set of edges
     */
    @Override
    public AbstractMathSet<Link<T>> edgeSet() {
        return image(p -> p.equals(tail()) ? new Link<>(p, head()) : new Link<>(p, next(p)));
    }

    /**
     * @return (AbstractOrderedTuple<T>) getter for immutable set of vertices
     */
    @Override
    public AbstractOrderedTuple<T> vertexSet() {
        return this;
    }
}
