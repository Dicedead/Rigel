package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class PartitionSet<T> extends MathSet<T> {

    private final IndexedSet<MathSet<T>, T> components;

    /**
     * Construct a partition where each equivalence class will be determined by its belonging Collection
     * @param data the underlying data
     */
    public PartitionSet(Collection<MathSet<T>> data) {
        super(unionOf(data).getData());
        components = new IndexedSet<>(data, elem -> data.stream().filter(subset -> subset.contains(elem)).findFirst().orElseThrow());
    }

    /**
     * A partition Set where each equivalence class is determined by the value of the indexer function
     * @param t the underlying data
     */
    public PartitionSet(IndexedSet<MathSet<T>, T> t) {
        super(unionOf(t.getData()));
        components = t;
    }

    /**
     * Main constructorl, building the equivalence classes from a relation
     * @param data the underlying data
     * @param areInRelation the equivalence relation used to partition the set
     */
    public PartitionSet(final MathSet<T> data, Relation.Equivalence<T> areInRelation) {
        this(data.image( (T elem1) -> areInRelation.partialApply(elem1).preImageOf(true).solveIn(data)).getData());
    }

    /**
     * A single equivalence class Partition Set
     * @param t the MathSet to copy
     */
    public PartitionSet(final MathSet<T> t) {
        this(Collections.singletonList(t));
    }

    /**
     *
     * @param t an element of this set
     * @return The equivalence class in which t lies
     */
    public MathSet<T> component(T t) {
        Preconditions.checkArgument(contains(t));
        return components.at(t);
    }

    /**
     *
     * @return The set of all equivalence classes
     */
    public MathSet<MathSet<T>> components() {
        return components;
    }

    /**
     * An element in a given component
     * @param component the component in which to get a representant
     * @return An element in this equivalence class
     */
    public T representing(MathSet<T> component)
    {
        Preconditions.checkArgument(components.contains(component));
        return component.getElement();
    }

    /**
     *
     * @return A set of representant for each equivalence class
     */
    public MathSet<T> representants()
    {
        return components.image(this::representing);
    }

    /**
     *
     * @param f the function to apply
     * @param <U> the type of the codomain of f
     * @return the image of each partition by this function
     */
    @Override
    public <U> PartitionSet<U> image(SetFunction<T, U> f) {
        return new PartitionSet<>(components.stream().map(C -> C.image(f)).collect(Collectors.toSet()));
    }

    /**
     * Allows to iterate over the equivalence classes
     * @param action the action to apply on each one
     */
    public void forEachComponent(Consumer<? super MathSet<T>> action) {
        components.getData().forEach(action);
    }

    /**
     *
     * @return the number of Equivalence classes
     */
    public int numberOfComponents(){return components.cardinality();}

    /**
     * Allows to iterate over the equivalence classes
     */
    public Stream<MathSet<T>> streamComponents()
    {
        return components.stream();
    }

}
