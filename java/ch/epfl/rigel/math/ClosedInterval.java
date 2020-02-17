package ch.epfl.rigel.math;


import ch.epfl.rigel.Preconditions;

import java.util.Locale;

public class ClosedInterval extends Interval {
    /**
     * @param inf lower bound of the interval
     * @param sup higher bound of the interval
     */
    private ClosedInterval(double inf, double sup) {
        super(inf, sup);
    }

    public static ClosedInterval of(double low, double high) {
        return new ClosedInterval(low, high);
    }

    /**
     * Returns a closed interval starting at zero, stopping at size if size is positive
     *
     * @param size is un unsigned double specifying the size of the interval
     * @return IllegalArgumentException if size is negative, the desired interval in the other case
     */
    public static ClosedInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        return new ClosedInterval(0, size);
    }

    /**
     * Clipping sends number to itsellf if it is in the interval, and else at the nearest bound
     *
     * @param v the number to clip
     * @return the clipped number
     */
    public double clip(double v) {
        /*This can be understood in this way : if v is smaller than the upper bound it may be in the interval so we
        choose it, then we compare it with the actual lower bound.
        If this is not the case we simply take the upper bound as it is superior to the lower bound.
         */
        return Math.max(Math.min(this.high(), v), this.low());
    }

    /**
     * @return interval in a standard form : [a, b]
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "[ %.2f, %.2f ]",
                this.low(),
                this.high());
    }

    /**
     * Checks whether a number is in the current interval
     *
     * @param v number to be checked
     * @return whether it is or not in the interval
     */
    @Override
    public boolean contains(double v) {
        return this.low() <= v && v <= this.high();
    }
}
