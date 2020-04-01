package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final static double EPSILON = 1e-8;

    //Handling multiple variables through currying
    private final static BiFunction<Integer, Integer, Function<double[], List<Boolean>>> COEFF_FORMAT =  (i, d) -> c ->
            List.of(areEqual(c[i], 0), (i == d - 1 || areEqual(c[i], 0)), areEqual(c[i + 1], 0));
            //Evaluates a bunch of conditions on the coefficients (if they're 0, if the next 0 is 0, if they're the constant
            //term. The boolean list it constructs is used in conjunction with the following currying Functions:

    private final static BiFunction<StringBuilder, Boolean, Function<String, StringBuilder>> SKIPP_COEFF =
                    (sb, b) -> s -> sb.append(b ? "" : s);
                    //Skips the coefficient if it is == 0

    private final static BiFunction<Integer, Integer, Function<double[], List<String>>> X_TO_POWER =  (i, d) -> c ->
            List.of("x", "^" + (d - i), (0 > c[i + 1]) ? "-" :  "+" );
            //Taking care of x^(power)(next sign) format

    private final static BiFunction<Integer, double[], String> NUMBER_FORMAT = (j, c) ->
            (areEqual(Math.abs(c[j]), 1) || areEqual(c[j], 0) ? "" : new DecimalFormat("##.########").format(Math.abs(c[j])));
            //If coefficient is non-zero, show first 8 decimals

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
     * @param currDegree (int) degree of subpolynomial
     * @return (double)
     */
    private double atRecur(final double x, final int currDegree) {
        return currDegree == 0 ? coefficients[0] : atRecur(x, currDegree - 1) * x + coefficients[currDegree];
    }

    /**
     * @return formatted polynomial
     */
    @Override
    public String toString() {
        return toStringRecursive(new StringBuilder((coefficients[0] < 0) ? "-" : ""), 0) //Treating highest degree term
                .append(NUMBER_FORMAT.apply(degree, coefficients)).toString(); //Setting number of decimals
    }

    /**
     * Auxiliary recursive method for toString String formatting
     *
     * @param format (StringBuilder) String being built before step i
     * @param i (int) incremental
     * @return (StringBuilder) String being built at step i / before step i+1
     */
    private StringBuilder toStringRecursive(StringBuilder format, final int i)
    {
        //Largely applying Functions defined above
        return (i == degree || degree == 0) ? format : toStringRecursive(IntStream.of(0, 1, 2)
                    .mapToObj(k -> SKIPP_COEFF.apply(k == 0 ? format.append(NUMBER_FORMAT.apply(i, coefficients)) : format,
                    COEFF_FORMAT.apply(i, degree).apply(coefficients).get(k)).apply(X_TO_POWER.apply(i, degree).apply(coefficients).get(k)))
                .collect(Collectors.toList()).get(2), i + 1);

        /*The IntStream(0,1,2) defines steps to take: (for k in this IntStream:)
             first (only when k=0), append the absolute value of the coefficient if non-zero; (NUMBER_FORMAT(...))
             second, check the necessary conditions for constructing the rest of the term*,
                     store them in a List<Boolean> after 2 currying calls used in third step; (COEFF_FORMAT(...))
             third: if k = 0: checks whether number is a zero, if so, omits next steps         (X_TO_POWER(...))
                    if k = 1: get the boolean value indicating whether nothing, x^i or x should be appended
                    if k = 2: checks whether next number is a zero or not in order to add a sign if it's not the case
          ... and then repeat by going down the list of coefficients (get(2) gets the resulting substring for this iteration).

           *we define "term" by the String [-](value!=1^0)x(^power!=1^0), ex: -5x^3, 1, x^2, x. */
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
