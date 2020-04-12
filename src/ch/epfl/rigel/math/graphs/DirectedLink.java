package ch.epfl.rigel.math.graphs;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class DirectedLink<T> extends Link<T> implements Arrow<T>{

    private final T start, end;

    public DirectedLink(final T start, final T end) {
        super(start, end);
        this.start = start;
        this.end = end;
    }

    public T getFirst()
    {
        return start;
    }

    public T getLast()
    {
        return end;
    }

}
