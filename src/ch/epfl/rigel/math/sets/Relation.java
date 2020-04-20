package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.graphs.Link;

import java.util.Set;

public interface Relation<T> {

    boolean areInRelation(T t, T u);

    interface Equivalence<T> extends Relation<T>{
        default boolean areInRelation(MathSet<T> pair)
        {
            Preconditions.checkArgument(pair.cardinality() == 2);
            return areInRelation(pair.getElement(), pair.minus(pair.getElement()).getElement());
        }
    }
}
