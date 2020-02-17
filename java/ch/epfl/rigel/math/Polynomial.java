package ch.epfl.rigel.math;
import ch.epfl.rigel.Preconditions;

import java.util.Locale;

public final class Polynomial {

    private final double[] coefficients;


    private Polynomial(double coefficientN, double... coefficients)
    {
        this.coefficients       = new double[coefficients.length + 1];
        this.coefficients[0]    = coefficientN;

        System.arraycopy( coefficients, 0, this.coefficients, 1, coefficients.length );

    }

    /**
     * Public instantiation of polynomial functions
     * @param coefficientN The highest coefficient, should not be null
     * @param coefficients The others coefficients
     * @return A polynomial with the specified coefficients in the reverse order of power
     */
    public static Polynomial of(double coefficientN, double... coefficients)
    {
        Preconditions.checkArgument(coefficientN != 0);
        return new Polynomial(coefficientN, coefficients);
    }

    /**
     * Interpretation of the polynomial function at a point using horner method
     * @param x point to interpret
     * @return result
     */
    public double at(double x)
    {
        double result = coefficients[0];

        for (int i = 1; i<= coefficients.length - 1; ++i)
        {
            result = result * x + coefficients[i];
        }

        return result;
    }


    /**
     *
     * @return formatted polynomial
     */
    @Override
    public String toString() {
        String s = "";

        for (int i = 0; i <= coefficients.length; ++i)
            s = s.concat("%fx^" + i + " +");

        //noinspection PrimitiveArrayArgumentToVarargsMethod
        return String.format(Locale.ROOT, s.substring(0, s.length() - 1), coefficients);
    }

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
