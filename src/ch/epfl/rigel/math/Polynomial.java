package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.text.DecimalFormat;
import java.util.function.Function;

/**
 * Polynomial object definition and associated tools
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Polynomial {

    private final double[] coefficients;
    private final int degree;
    private final static double EPSILON = 1e-6;

    private Polynomial(double coefficientN, double... coefficients) {

        this.coefficients = new double[coefficients.length + 1];
        this.coefficients[0] = coefficientN;
        degree = this.coefficients.length - 1;

        System.arraycopy(coefficients, 0, this.coefficients, 1, coefficients.length);

    }

    /**
     * Public instantiation of polynomial functions
     *
     * @param coefficientN The highest coefficient, should not be null
     * @param coefficients The others coefficients
     * @return A polynomial with the specified coefficients in the reverse order of power
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        Preconditions.checkArgument(coefficientN != 0);
        return new Polynomial(coefficientN, coefficients);
    }

    /**
     * Interpretation of the polynomial function at a point using (recursive) horner method
     *
     * @param x (double) point to interpret
     * @return (double) polynomial computed at desired point
     */
    public double at(double x) {
        return atRecur(x, degree);
    }

    /**
     * Auxiliary recursive method applying horner's scheme
     *
     * @param x (double) point to interpret
     * @param c (int) degree of subpolynomial
     * @return (double)
     */
    private double atRecur(double x, int c) {
        return c == 0 ? coefficients[0] : atRecur(x, c - 1) * x + coefficients[c];
    }

    /**
     * @return formatted polynomial
     */
    @Override
    public String toString() {

        //Highest degree term's sign initialization
        final StringBuilder format = new StringBuilder((coefficients[0] < 0) ? "-" : "");

        //Template for coefficient formatting
        final Function<Integer, String> f = (Integer i) ->
                ((areEqual(Math.abs(coefficients[i]), 1) || areEqual(coefficients[i], 0)) ? "" :
                        new DecimalFormat("##.########").format(Math.abs(coefficients[i])));

        //Main loop: constructing the string
        for (int i = 0; i <= degree - 1; ++i) {
            /*
            Concatenation of the coefficient, its associated x to the power of degree-i, and the next coefficient's sign.
            Steps:
               -Formatting using Function f
               -Checking whether the coefficient != 0 (0 -> x is skipped)
               -Appending the '^' sign and the degree if degree != 0 ^ degree != 1
               -Appending the next coefficient's sign (if it exists ^ is != 0)
             */
            format.append(f.apply(i))
                    .append(areEqual(coefficients[i], 0) ? "" : "x")
                    .append((i == degree - 1 || areEqual(coefficients[i], 0)) ? "" : "^" + (degree - i))
                    .append(areEqual(coefficients[i + 1], 0) ? "" : (0 > coefficients[i + 1]) ? "-" : "+");

        }

        //Treatment of the (eventual) constant term
        return format.append(f.apply(degree)).toString();
    }

    /**
     * Testing equality between two double values using a small epsilon
     *
     * @param value1 (double)
     * @param value2 (double)
     * @return (boolean) boolean of equality
     */
    private boolean areEqual(double value1, double value2) {
        return (Math.abs(value1 - value2)) <= EPSILON;
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
        //        "allows it.");
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
