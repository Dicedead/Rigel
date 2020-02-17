package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

public final class Polynomial {

    private final double[] coefficients;
    private final static double EPSILON = 1e-6;
    private final static int NBR_DECIMALS = 3;

    private Polynomial(double coefficientN, double... coefficients) {
        this.coefficients = new double[coefficients.length + 1];
        this.coefficients[0] = coefficientN;

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
        StringBuilder s = new StringBuilder();

        for (int i = 0; i <= coefficients.length - 1; ++i) {

            if (valuesAreEqual(coefficients[i], Math.round(coefficients[i]))) {
                formatPolynomialTerm(s, String.format(Locale.ROOT, "%.0f",
                        Math.abs(coefficients[i])), coefficients[i], i);
            } else {
                formatPolynomialTerm(s, String.format(Locale.ROOT, "%." +
                        NBR_DECIMALS + "f", Math.abs(coefficients[i])), coefficients[i], i);
            }

        }

        if (s.substring(s.length() - 1).equals("0")) {
            s.delete(s.length() - 3, s.length());
        }

        return s.substring(0);
    }

    /**
     * Auxiliary string building method taking care of fine cosmetic details
     *
     * @param processedString StringBuilder that's being worked on
     * @param numberStr       String representation of the coefficient's absolute value
     * @param numberValue     the coefficient's numerical value
     * @param i               current iteration
     */
    private void formatPolynomialTerm(StringBuilder processedString, String numberStr, double numberValue, int i) {

        String powerStr = (coefficients.length - 1 - i == 1) ? "" : "^" + (coefficients.length - 1 - i);

        if (!valuesAreEqual(numberValue, 0) && !valuesAreEqual(numberValue, 1)) {
            processedString.append(numberStr).append("x").append(powerStr);
        } else if (valuesAreEqual(numberValue, 1)) {
            processedString.append("x").append(powerStr);
        }

        String nextSign;
        if (i + 1 == coefficients.length || valuesAreEqual(coefficients[i + 1], 0)) {
            nextSign = "";
        } else {
            nextSign = (coefficients[i + 1] > 0) ? "+" : "-";
        }
        processedString.append(nextSign);
    }

    /**
     * Double comparator
     *
     * @param value1 First double
     * @param value2 Second double
     */
    private boolean valuesAreEqual(double value1, double value2) {
        return (Math.abs(value1 - value2) < EPSILON);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
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
