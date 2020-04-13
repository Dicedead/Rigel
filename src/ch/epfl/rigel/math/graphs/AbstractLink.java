package ch.epfl.rigel.math.graphs;

import javafx.util.Pair;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Abstraction of an edge, or link between two elements of the same type on a graph
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class AbstractLink<T> {

    private final Set<T> unorderedPair;

    /**
     * AbstractLink constructor: ties 2 elements together
     *
     * @param t (T)
     * @param u (T)
     */
    public AbstractLink(final T t, final T u) {
        this.unorderedPair = Set.of(t, u);
    }

    /**
     * AbstractLink constructor: ties the 2 elements in a pair together (with no order)
     *
     * @param p (Pair<T,T>)
     */
    public AbstractLink(final Pair<T, T> p) {
        this.unorderedPair = Set.of(p.getKey(), p.getValue());
    }

    /**
     * Gets one element of the link starting from the other linked element
     *
     * @param start (T) the object that's known to be in the link
     * @return (T) the object 'start' is tied to
     * @throws NoSuchElementException if 'start' is not in the link
     */
    public T next(final T start) {
        return unorderedPair.stream().filter(l -> !l.equals(start)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    /**
     * @return (Set<T>) gets the two linked elements
     */
    public Set<T> getPoints() {
        return unorderedPair;
    }
}
