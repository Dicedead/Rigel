package ch.epfl.rigel.math.sets.abtract;

import ch.epfl.rigel.Preconditions;

public interface AbstractPartitionSet<T> extends AbstractMathSet<T>{
     /**
     *
     * @param t an element of this set
     * @return The equivalence class in which t lies
     */
     default AbstractMathSet<T> component(T t) {
        Preconditions.checkArgument(contains(t));
        return components().suchThat(p -> p.contains(t)).getElement();
    }
    /**
     *
     * @return The set of all equivalence classes
     */
    AbstractMathSet<AbstractMathSet<T>> components();

    /**
     * An element in a given component
     * @param component the component in which to get a representant
     * @return An element in this equivalence class
     */
    default T representing(AbstractMathSet<T> component)
    {
        Preconditions.checkArgument(components().contains(component));
        return component.getElement();
    }

    /**
     *
     * @return A set of representant for each equivalence class
     */
    default AbstractMathSet<T> representants()
    {
        return components().image(this::representing);
    }

    /**
     *
     * @return the number of Equivalence classes
     */
    default int numberOfComponents(){return components().cardinality();}

}
