package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.implement.MathSet;
import ch.epfl.rigel.math.sets.implement.OrderedTuple;
import ch.epfl.rigel.math.sets.properties.SetFunction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Abstraction of a cyclic graph
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class Cycle<T> extends OrderedTuple<T> implements Graph<T, AbstractOrderedTuple<T>> {

    private final T start;
    private final T end;

    /**
     * Main Cycle constructor, 'tying' both ends of given list
     *
     * @param listedData (List<T>)
     */
    public Cycle(List<T> listedData) {
        super(listedData);
        start = listedData.get(0);
        end = listedData.get(listedData.size() - 1);
    }

    /**
     * Alternate Cycle constructor, 'tying' both ends of given ordered tuple
     *
     * @param collect (AbstractOrderedTuple<T>)
     */
    public Cycle(AbstractOrderedTuple<T> collect) {
        super(collect);
        start = collect.head();
        end = collect.tail();
    }

    /**
     * @see Graph#getNeighbours(Object)  
     */
    @Override
    public Optional<AbstractOrderedTuple<T>> getNeighbours(T point) {
        return Optional.of(new OrderedTuple<>(prev(point), next(point)));
    }

    /**
     * @see Graph#on(AbstractMathSet)
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

    /**
     * @see Cycle#flow(int)
     * Applies flow on the index of given point, the given chooser function is irrelevant
     *
     * @param chooser irrelevant
     * @param point (T) given point in this Cycle
     * @return (AbstractOrderedTuple<T>)
     */
    @Override
    public AbstractOrderedTuple<T> flow(SetFunction<AbstractOrderedTuple<T>, T> chooser, T point) {
        return flow(indexOf(point));
    }

    /**
     * @see Cycle#flow(int)
     * Applies flow on the index of given point
     *
     * @param point (T) given point in this Cycle
     * @return (AbstractOrderedTuple<T>)
     */
    public AbstractOrderedTuple<T> flow(T point) {
        return flow(indexOf(point));
    }

    /**
     * @param index (int)
     * @return (AbstractOrderedTuple<T>) points in this cycle which's index is smaller or equal to index mod cardinality
     *         of the cycle
     */
    public AbstractOrderedTuple<T> flow(int index) {
        return new OrderedTuple<>(toList().subList(0, index % cardinality()));
    }

    /**
     * @return (T) getter for starting point of the cycle (chosen at construction)
     */
    public T getStart() {
        return start;
    }

    /**
     * @return (T) getter for end point of the cycle (chosen at construction)
     */
    public T getEnd() {
        return end;
    }

    /**
     * @see Graph#edgeSet()
     */
    @Override
    public AbstractMathSet<Link<T>> edgeSet() {
        return image(p -> p.equals(tail()) ? new Link<>(p, head()) : new Link<>(p, next(p)));
    }

    /**
     * @see Graph#vertexSet()
     */
    @Override
    public AbstractOrderedTuple<T> vertexSet() {
        return this;
    }
}
