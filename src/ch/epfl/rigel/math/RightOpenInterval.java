package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * Right open interval representation and methods
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class RightOpenInterval extends Interval {

    /**
     * @param inf (double) lower bound of the interval
     * @param sup (double) higher bound of the interval
     * @throws IllegalArgumentException if interval is empty
     */
    private RightOpenInterval(double inf, double sup) {
        super(inf, sup);
    }

    /**
     * Constructs a right open interval [low;high[
     *
     * @param low  (double) lower bound of the interval
     * @param high (double) higher bound of the interval
     * @return (RightOpenInterval)
     * @throws IllegalArgumentException if low >= high
     */
    public static RightOpenInterval of(double low, double high) {
        Preconditions.checkArgument(high > low);
        return new RightOpenInterval(low, high);
    }

    /**
     * Returns a closed interval starting at zero, stopping at size if size is positive (factory constructor)
     *
     * @param size (double) is un unsigned double specifying the size of the interval
     * @return (RightOpenInterval) the desired interval if size is positive
     * @throws IllegalArgumentException if size <= 0
     */
    public static RightOpenInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        double halfSize = size / 2.0;
        return new RightOpenInterval(-halfSize, halfSize);
    }

    /**
     * @return (String) interval in a standard form : [a, b[
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "[ %.2f, %.2f [",
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
        return this.low() <= v && v < this.high();
    }

    /**
     * Reduces a value v to a semi open interval
     *
     * @param v (double) number to be reduced
     * @return (double) reduced number
     */
    public double reduce(double v) {
        return contains(v) ? v : v - this.size() * Math.floor((v - this.low()) / this.size());
    }
}
