package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.OrderedPair;
import javafx.util.Pair;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Abstraction of an edge, or link between two elements of the same type on a graph
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class Link<T> extends OrderedPair<T> {

    /**
     * Link constructor: ties 2 elements together
     *
     * @param t (T)
     * @param u (T)
     */
    public Link(final T t, final T u) {
        super(t, u);
    }

    /**
     * Link constructor: ties the 2 elements in a pair together (with no order)
     *
     * @param p (Pair<T,T>)
     */
    public Link(final Pair<T, T> p) {

        super(p.getKey(), p.getValue());
    }

    /**
     * Gets one element of the link starting from the other linked element
     *
     * @param start (T) the object that's known to be in the link
     * @return (T) the object 'start' is tied to
     * @throws NoSuchElementException if 'start' is not in the link
     */
    public T next(final T start) {
        return stream().filter(l -> !l.equals(start)).findFirst().orElseThrow(NoSuchElementException::new);
    }
}
