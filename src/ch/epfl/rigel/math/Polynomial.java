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
    private static int degree;
    private final static double EPSILON = 1e-6;

    private Polynomial(double coefficientN, double... coefficients)
    {

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
     * Interpretation of the polynomial function at a point using horner method
     *
     * @param x point to interpret
     * @return result
     * @throws NullPointerException if Polynomial is declared but has not been created with Polynomial.of
     */
    public double at(double x) {

        double result = coefficients[0];

        for (int i = 1; i <= coefficients.length - 1; ++i) {
            result = result * x + coefficients[i];
        }

        return result;
    }

    /**
     * @return formatted polynomial
     */
    @Override
    public String toString() {

        //Highest degree term's sign initialization
        StringBuilder format = new StringBuilder((coefficients[0] < 0) ? "-" : "");

        //Template for coefficient formatting
        Function<Integer, String> f = (Integer i) ->
                ((isEqual(Math.abs(coefficients[i]), 1) || isEqual(coefficients[i], 0)) ? "" :
                new DecimalFormat("##.########").format(Math.abs(coefficients[i])));

        //Main loop: constructing the string
        for (int i = 0; i <= degree - 1; ++i)
        {
            /*
            Concatenation of the coefficient, its associated x to the power of degree-i, and the next coefficient's sign.
            Steps:
               -Formatting using Function f
               -Checking whether the coefficient != 0 (0 -> x is skipped)
               -Appending the '^' sign and the degree if degree != 0 ^ degree != 1
               -Appending the next coefficient's sign (if it exists ^ is != 0)
             */
            format.append(f.apply(i)).append(isEqual(coefficients[i], 0) ? "" : "x")
                    .append((i == degree - 1 || isEqual(coefficients[i], 0)) ? "" : "^" + (degree - i))
                    .append(isEqual(coefficients[i + 1], 0) ? "" : (0 > coefficients[i + 1]) ? "-" : "+");

        }

        //Treatment of the (eventual) constant term
        return format.append(f.apply(degree)).toString();
    }

    private boolean isEqual(double value1, double value2) {
        return (Math.abs(value1 - value2)) <= EPSILON;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
        //        "allows it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        //System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
        //        "allows it.");
        throw new UnsupportedOperationException();
    }
}