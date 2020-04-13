package ch.epfl.rigel.math.graphs;

import javafx.util.Pair;

/**
 * Representation of edges for undirected graphs on elements of the same type
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class UndirectedLink<T> extends AbstractLink<T>{

    /**
     * UndirectedLink constructor
     *
     * @param t (T) first element
     * @param u (T) second element
     * Note that as this is an UndirectedLink, the order does not matter
     */
    public UndirectedLink(final T t, final T u) {
        super(t, u);
    }

    /**
     * UndirectedLink constructor
     *
     * @param p (Pair<T,T>) the two elements in the pair become linked
     */
    public UndirectedLink(final Pair<T, T> p) {
        super(p);
    }
}