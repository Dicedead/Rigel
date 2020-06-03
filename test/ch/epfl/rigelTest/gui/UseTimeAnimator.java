package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.math.Polynomial;
import javafx.scene.control.Button;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class UseTimeAnimator {
    public static void main(String[] args) {
        int errors = 0;
        for (int i = 0; i < 2000; ++i) {
            double random = Math.random();
            if (Math.abs(Math.asin(random) - asin3(random)) > 1e-9) {
                ++errors;
                System.out.println(random);
                System.out.println(StrictMath.asin(random));
                System.out.println(asin3(random));
                System.out.println("=======================");
            }
        }
        System.out.println(errors);
        System.out.println(StrictMath.sqrt(2));
        System.out.println(oneMinSqrt(2));
        new Button().fire();
    }

    private final static double a0 = 1.570796305;
    private final static double a1 = -0.2145988016;
    private final static double a2 = 0.889789874;
    private final static double a3 = -0.0501743046;
    private final static double a4 = .0308918810;
    private final static double a5 = -0.0170881256;
    private final static double a6 = 0.0066700901;
    private final static double a7 = -0.0012624911;

    private final static Polynomial ARCSIN_POLY = Polynomial.of(a5, a4, a3, a2, a1, a0);
    private final static Polynomial SQRT_POLY = Polynomial.of(-7/256d, -5/128d, -1/16d, -1/8d,
            -1/2d, 1);
    private static double oneMinSqrt(double x) {

        double guess = x;
        double diff = Double.MAX_VALUE;

        while (diff >= 2e-4) {
            double new_guess = (guess*guess + x - 1)/guess;
            diff = abs(new_guess - guess);
            guess = new_guess;
        }


        return guess;
    }
    private static double asin3(double x) {

        return x < 0 ? -1 * (PI/2 - StrictMath.sqrt(1 - abs(x)) * ARCSIN_POLY.at(abs(x)))
                 : PI/2 - StrictMath.sqrt(1 - x) * ARCSIN_POLY.at(x);
    }
}
