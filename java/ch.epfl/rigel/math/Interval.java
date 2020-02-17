package ch.epfl.rigel.math;

import java.util.Objects;

public abstract class Interval {

    protected Interval(double inf, double sup)
    {
        this.inf = inf;
        this.sup = sup;
    }

    final private double inf, sup;

    public double low()
    {
        return inf;
    }

    public double high()
    {
        return sup;
    }

    public double size()
    {
        return sup - inf;
    }

    abstract boolean contains(double v);

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
