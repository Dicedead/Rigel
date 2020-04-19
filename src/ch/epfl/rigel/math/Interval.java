package ch.epfl.rigel.math;

/**
 * Abstraction of a general interval
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class Interval {

    final private double inf, sup;

    /**
     * @param inf (double) lower bound of the interval
     * @param sup (double) higher bound of the interval
     */
    protected Interval(final double inf, final double sup) {
        this.inf = inf;
        this.sup = sup;
    }

    /**
     * @return value of the lower bound of the interval
     */
    public double low() {
        return inf;
    }

    /**
     * @return value of the higher bound of the interval
     */
    public double high() {
        return sup;
    }

    /**
     * @return size of the interval
     */
    public double size() {
        return sup - inf;
    }

    abstract public boolean contains(double v);

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
