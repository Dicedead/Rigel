package ch.epfl.rigel.math.sets.concrete;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.abtract.AbstractOrderedSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.properties.Relation;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class OrderedSet<T> extends MathSet<T> implements AbstractOrderedSet<T> {
    private final Relation.Order<T> comparator;


    /**
     * The main constructor
     * @param m the underlying MathSet
     * @param comparator the order associated with this set
     */
    public OrderedSet(MathSet<T> m,Relation.Order<T> comparator) {
        super(m);
        this.comparator = comparator;
    }

    @Override
    public Relation.Order<T> getComparator() {
        return comparator;
    }

}
