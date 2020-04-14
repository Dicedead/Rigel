package ch.epfl.rigel.math.graphs;

import javafx.util.Pair;

/**
 * Representation of arrow edges for directed graphs on elements of the same type
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class DirectedLink<T> extends Link<T> {

    private final T start, end;

    /**
     * Arrow/DirectedLink constructor
     *
     * @param start (T) start of the arrow
     * @param end   (T) tail of the arrow
     */
    public DirectedLink(final T start, final T end) {
        super(start, end);
        this.start = start;
        this.end = end;
    }

    /**
     * Arrow/DirectedLink alternative constructor
     *
     * @param p (Pair<T,T>) p's key becomes the start and p's value the end of the arrow
     */
    public DirectedLink(final Pair<T, T> p) {
        super(p);
        this.start = p.getKey();
        this.end = p.getValue();
    }

    /**
     * @return (T) start getter
     */
    public T getFirst() {
        return start;
    }

    /**
     * @return (T) end getter
     */
    public T getLast() {
        return end;
    }

}
