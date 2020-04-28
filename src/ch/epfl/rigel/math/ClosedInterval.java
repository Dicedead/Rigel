package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * Closed interval representation and methods
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ClosedInterval extends Interval {
    /**
     * @param inf (double) lower bound of the interval
     * @param sup (double) higher bound of the interval
     * @throws IllegalArgumentException if interval is empty
     */
    private ClosedInterval(double inf, double sup) {
        super(inf, sup);
    }

    /**
     * Constructs a closed interval [low;high] (factory constructor)
     *
     * @param low  (double) lower bound of the interval
     * @param high (double) higher bound of the interval
     * @return (ClosedInterval)
     * @throws IllegalArgumentException if low >= high
     */
    public static ClosedInterval of(double low, double high) {
        Preconditions.checkArgument(high > low);
        return new ClosedInterval(low, high);
    }

    /**
     * Returns a closed interval starting at zero, stopping at size if size is positive (factory constructor)
     *
     * @param size (double) is un unsigned double specifying the size of the interval
     * @return (ClosedInterval) the desired interval if size is positive
     * @throws IllegalArgumentException if size is <= 0
     */
    public static ClosedInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        double halfSize = size / 2.0;
        return new ClosedInterval(-halfSize, halfSize);
    }

    /**
     * Clipping sends number to itself if it is in the interval, and else at the nearest bound
     *
     * @param v (double) the number to clip
     * @return (double) the clipped number
     */
    public double clip(double v) {
        /*This can be understood in this way : if v is smaller than the upper bound it may be in the interval so we
        choose it, then we compare it with the actual lower bound.
        If this is not the case we simply take the upper bound as it is superior to the lower bound.
         */
        return Math.max(Math.min(this.high(), v), this.low());
    }

    /**
     * @return (String) interval in a standard form : [a, b]
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
     * @param v (double) number to be checked
     * @return (boolean) whether it is or not in the interval
     */
    @Override
    public boolean contains(double v) {
        return this.low() <= v && v <= this.high();
    }
}
