package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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
    private final static BiFunction<Boolean, String, Function<String, String>> g = (b, s) -> l -> b ? l : s;
    private final static BiFunction<StringBuilder, Boolean, Function<String, StringBuilder>> h = (sb, b) -> s -> sb.append(g.apply(b, s).apply(""));

    private final static Function<Integer, Function<List<Double>, String>> f =
            (Integer j) -> c -> (g.apply(areEqual(Math.abs(c.get(j)), 1) || areEqual(c.get(j), 0) ,
                    new DecimalFormat("##.########").format(Math.abs(c.get(j)))).apply(""));

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
    private double atRecur(final double x, final int c) {
        return c == 0 ? coefficients[0] : atRecur(x, c - 1) * x + coefficients[c];
    }

    /**
     * @return formatted polynomial
     */
    @Override
    public String toString() {
        //Highest degree term's sign initialization
        return toRecurence(new StringBuilder((coefficients[0] < 0) ? "-" : ""), 0).toString();
    }

    private StringBuilder toRecurence (final StringBuilder format,final int i)
    {
        final Function<Integer, String> t = j -> f.apply(j).apply(DoubleStream.of(coefficients).boxed().collect(Collectors.toUnmodifiableList()));
        //Treatment of the (eventual) constant term
        return i == degree ? format.append(t.apply(degree)) :
                toRecurence(h.apply(h.apply(h.apply(format.append(f.apply(i)), areEqual(coefficients[i], 0)).apply("x"),
                (i == degree - 1 || areEqual(coefficients[i], 0))).apply("^" + (degree - i)),
                areEqual(coefficients[i + 1], 0)).apply(g.apply((0 > coefficients[i + 1]),"+" ).apply("-")), i + 1);
    }

    /**
     * Testing equality between two double values using a small epsilon
     *
     * @param value1 (double)
     * @param value2 (double)
     * @return (boolean) boolean of equality
     */
    private static boolean areEqual(double value1, double value2) {
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
