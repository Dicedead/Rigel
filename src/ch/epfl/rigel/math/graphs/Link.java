package ch.epfl.rigel.math.graphs;

import javafx.util.Pair;

import java.util.Set;

public class Link<T> implements Arrow<T>{

    protected final Set<T> p;

    public Link(final T t, final T u)
    {
        p = Set.of(t, u);
    }

    public T next (final T start)
    {
        return p.stream().filter(l -> !l.equals(start)).findFirst().orElseThrow();
    }

    public Link(final Pair<T, T> p)
    {
        this.p = Set.of(p.getKey(), p.getValue());
    }

    public Set<T> getPoints()
    {
        return p;
    }

}