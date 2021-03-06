package ch.epfl.rigel.math.sets.implement;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractPartitionSet;
import ch.epfl.rigel.math.sets.properties.Relation;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of a set equipped with an equivalence relation
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class PartitionSet<T> extends MathSet<T> implements AbstractPartitionSet<T> {

    private final IndexedSet<AbstractMathSet<T>, T> components;

    /**
     * Construct a partition where each equivalence class will be determined by its belonging Collection
     * @param data the underlying data
     */
    public PartitionSet(Collection<AbstractMathSet<T>> data) {
        super(AbstractMathSet.unionOf(data).getRawData());
        components = new IndexedSet<>(data, elem -> data.stream().filter(subset -> subset.contains(elem)).findFirst().orElseThrow());
    }

    /**
     * A partition Set where each equivalence class is determined by the value of the indexer function
     * @param t the underlying data
     */
    public PartitionSet(IndexedSet<AbstractMathSet<T>, T> t) {
        super(AbstractMathSet.unionOf(t.getRawData()));
        components = t;
    }

    /**
     * Main constructor, building the equivalence classes from a relation
     * @param data (AbstractMathSet<T>) the underlying data
     * @param areInRelation the equivalence relation used to partition the set
     */
    public PartitionSet(AbstractMathSet<T> data, Relation.Equivalence<T> areInRelation) {
        this(data.image( (T elem1) -> areInRelation.partialApply(elem1).preImageOf(true).solveIn(data)).getRawData());
    }

    /**
     * A single equivalence class Partition Set
     * @param t the MathSet to copy
     */
    public PartitionSet(AbstractMathSet<T> t) {
        this(Collections.singletonList(t));
    }

    /**
     * @return The set of all equivalence classes
     */
    @Override
    public AbstractMathSet<AbstractMathSet<T>> components() {
        return components;
    }
}
