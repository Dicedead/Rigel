package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.properties.Relation;

import java.util.Set;

/**
 * Abstraction of a set with orderable elements
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface AbstractOrderedSet<T> extends AbstractMathSet<T> {

    /**
     * Set ordering function
     *
     * @return (Relation.Order<T>) get the set ordering function
     */
    Relation.Order<T> getComparator();

    default Relation.Ordering compare(T t, T u)
    {
        Preconditions.checkArgument(contains(t) && contains(u));
        return getComparator().compare(t, u);
    }

    /**
     *
     * @return the minimal elements according to the order
     */
    default AbstractMathSet<T> min()
    {
        return suchThat(p -> getComparator().partialApply(p).apply(this).getRawData().equals(Set.of(Relation.Ordering.LESS)));
    }
    /**
     *
     * @return the maximal elements according to the order
     */
    default AbstractMathSet<T> max()
    {
        return suchThat(p -> getComparator().partialApply(p).apply(this).getRawData().equals(Set.of(Relation.Ordering.LESS)));
    }

    /**
     * All the elements greater than t according to the order
     * @param t the element to compare
     * @return all the elements greater than t in this set
     */
    default AbstractMathSet<T> moreThan(T t)
    {
        return suchThat(p -> getComparator().partialApply(t).apply(p) == (Relation.Ordering.LESS));
    }


    /**
     * All the elements less than t according to the order
     * @param t the element to compare
     * @return all the elements less than t in this set
     */
    default AbstractMathSet<T> lessThan(T t)
    {
        return suchThat(p -> getComparator().partialApply(t).apply(p) == (Relation.Ordering.GREATER));
    }

    /**
     * All the elements equal to t according to the order
     * @param t the element to compare
     * @return all the elements equal to t in this set
     */
    default AbstractMathSet<T> equalsTo(T t)
    {
        return suchThat(p -> getComparator().partialApply(t).apply(p) == (Relation.Ordering.EQUAL));
    }
}
