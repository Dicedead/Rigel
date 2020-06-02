package ch.epfl.rigel.math.sets.properties;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.implement.MathSet;

import static java.lang.Integer.signum;

/**
 * Definition of a relation and some subtypes of relations
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
@FunctionalInterface
public interface Relation<T, U> {

    /**
     * Concrete realization of a Relation: an expression between arbitrary values of the same type
     *
     * @param t (T) element 1
     * @param u (T) element 2
     * @return (U) result of relation computation
     */
    U areInRelation(T t, T u);

    /**
     * Allows to compare an element to a set
     *
     * @param t (T)
     * @return (SetFunction<T, U>) setFunction for finding all elements in some set related to given element t
     */
    default SetFunction<T, U> partialApply(T t)
    {
        return l -> areInRelation(t, l);
    }

    interface Equivalence<T> extends Relation<T, Boolean>{
        default boolean areInRelation(MathSet<T> pair)
        {
            Preconditions.checkArgument(pair.cardinality() == 2);
            T t = pair.getElementOrThrow();
            return areInRelation(t, pair.minus(t).getElementOrThrow());
        }
    }

    enum Ordering {
        LESS, EQUAL, GREATER;
        public static Ordering of(int i)
        {
            switch (signum(i))
            {
                case -1 : return LESS;
                case 0 : return EQUAL;
                case 1 : return GREATER;
            }
            throw new IllegalArgumentException();
        }
    }

    interface Order<T> extends Relation<T, Ordering>
    {
        default Ordering compare(T t, T u)
        {
            return areInRelation(t, u);
        }
    }
}
