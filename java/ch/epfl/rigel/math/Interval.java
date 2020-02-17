package ch.epfl.rigel.math;

public abstract class Interval {

    /**
     *
     * @param inf lower bound of the interval
     * @param sup higher bound of the interval
     */
    protected Interval(final double inf, final double sup)
    {
        this.inf = inf;
        this.sup = sup;
    }

    final private double inf, sup;

    /**
     * Getter for lower bound
     * @return value of the lower bound of the interval
     */
    public double low()
    {
        return inf;
    }
    /**
     * Getter for higher bound
     * @return value of the higher bound of the interval
     */
    public double high()
    {
        return sup;
    }
    /**
     * Getter for size
     * @return size of the interval
     */
    public double size()
    {
        return sup - inf;
    }

    abstract boolean contains(double v);

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o)
    {
        System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
                "allows it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
                "allows it.");
        throw new UnsupportedOperationException();
    }
}
