package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;

import static java.lang.Integer.signum;

@FunctionalInterface
public interface Relation<T, U> {

    U areInRelation(T t, T u);
    default SetFunction<T, U> partialApply(T t)
    {
        return l -> areInRelation(t, l);
    }
    @FunctionalInterface
    interface Equivalence<T> extends Relation<T, Boolean>{
        default boolean areInRelation(MathSet<T> pair)
        {
            Preconditions.checkArgument(pair.cardinality() == 2);
            return areInRelation(pair.getElement(), pair.minus(pair.getElement()).getElement());
        }
    }
    enum COMP {
        LESS, EQUAL, GREATER;
        public static COMP of(int i)
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

    @FunctionalInterface
    interface Order<T> extends Relation<T, COMP>
    {
        default COMP compare(T t, T u)
        {
            return areInRelation(t, u);
        }
    }
}
