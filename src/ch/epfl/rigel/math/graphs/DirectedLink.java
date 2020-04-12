package ch.epfl.rigel.math.graphs;

public class DirectedLink<T> extends Link<T> implements Arrow<T>{

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
