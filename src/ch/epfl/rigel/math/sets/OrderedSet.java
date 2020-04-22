package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;
/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class OrderedSet<T> extends MathSet<T> {
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

    /**
     * Applies the order relation on a given element
     * @param t the first operand of the comparaison
     * @param u the second operand of the comparaison
     * @return Whether t is greater, equal or less than u
     */
    public Relation.COMP compare(T t, T u)
    {
        Preconditions.checkArgument(contains(t) && contains(u));
        return comparator.compare(t, u);
    }

    /**
     *
     * @return the minimal elements according to the order
     */
    public MathSet<T> min()
    {
        return suchThat(p -> comparator.partialApply(p).apply(this).equals(of(Relation.COMP.LESS)));
    }
    /**
     *
     * @return the maximal elements according to the order
     */
    public MathSet<T> max()
    {
        return suchThat(p -> comparator.partialApply(p).apply(this).equals(of(Relation.COMP.GREATER)));
    }

    /**
     * All the elements greater than t according to the order
     * @param t the element to compare
     * @return all the elements greater than t in this set
     */
    public MathSet<T> moreThan(T t)
    {
        return suchThat(p -> comparator.partialApply(t).apply(p) == (Relation.COMP.LESS));
    }


    /**
     * All the elements less than t according to the order
     * @param t the element to compare
     * @return all the elements less than t in this set
     */
    public MathSet<T> lessThan(T t)
    {
        return suchThat(p -> comparator.partialApply(t).apply(p) == (Relation.COMP.GREATER));
    }

    /**
     * All the elements equal to t according to the order
     * @param t the element to compare
     * @return all the elements equal to t in this set
     */
    public MathSet<T> equalsTo(T t)
    {
        return suchThat(p -> comparator.partialApply(t).apply(p) == (Relation.COMP.EQUAL));
    }

}
