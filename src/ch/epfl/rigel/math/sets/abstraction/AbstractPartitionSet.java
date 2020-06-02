package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.Preconditions;

/**
 * Abstraction of a set equipped with an equivalence relation that thus partitions it
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface AbstractPartitionSet<T> extends AbstractMathSet<T>{

    /**
     * The maximal component of the given element, ie all the elements in this set that are related to given element
     *
     * @param t an element of this set
     * @return The equivalence class in which t lies
     */
     default AbstractMathSet<T> component(T t) {
        Preconditions.checkArgument(contains(t));
        return components().suchThat(p -> p.contains(t)).getElementOrThrow();
    }

    /**
     * @return (AbstractMathSet<AbstractMathSet<T>>) the set of all equivalence classes
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
        return component.getElementOrThrow();
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
