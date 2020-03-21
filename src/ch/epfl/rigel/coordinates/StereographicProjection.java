package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.function.Function;

import static java.lang.Math.*;

/**
 * StereographicProjection function like class
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final HorizontalCoordinates centerOfProjection;
    private final double cosPhi1, sinPhi1;

    public StereographicProjection(HorizontalCoordinates center) {
        centerOfProjection = center;
        cosPhi1 = cos(center.alt());
        sinPhi1 = sin(center.alt());
    }

    /**
     * Horizontal -> Cartesian
     *
     * @param azAlt input horizontal coordinates
     * @return Cartesian coordinate corresponding to azAlt
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {

        double lambda = azAlt.az() - centerOfProjection.az();

        double phi = azAlt.alt();
        double sinP = 2 * sin(phi);

        double term1 = cos(lambda - phi) + cos(lambda + phi);
        double num =  cosPhi1 * sinP - sinPhi1 * term1;
        double den = 1 / (sinPhi1 * sinP + cosPhi1 * term1 + 2); // +2 and remove 1/2

        return CartesianCoordinates.of(sin(lambda) * cos(phi) * 2 * den, num * den);
    }

    /**
     * @param hor he corresponding parallel on earth
     * @return the center of the circle projected from parallel
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return CartesianCoordinates.of(0, cosPhi1 / (sin(hor.alt()) + sinPhi1));
    }

    /**
     * @param parallel the corresponding parallel on earth
     * @return the radius of the circle projected from parallel
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        return cos(parallel.alt()) / (sin(parallel.alt()) + sinPhi1);
    }

    /**
     * @param rad size of the sphere
     * @return diameter of the projected circle of angular size rad
     */
    public double applyToAngle(double rad) {
        return 2 * tan(rad / 4);
    }

    /**
     * Cartesian -> Horizontal
     *
     * @param xy input cartesian coordinates
     * @return Horizontal corresponding to azAlt
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double y = xy.y();
        double x = xy.x();
        double p2 = x * x + y * y;
        double p = Math.sqrt(p2);

        double den = 1 / (p2 + 1);
        double sinC = 2 * p * den;
        double cosC = (1 - p2) * den;
        double term = y * sinC;
        return HorizontalCoordinates.of(
                Angle.normalizePositive((centerOfProjection.az() + atan2(x * sinC, p * cosPhi1 * cosC - term * sinPhi1))),
                (asin(cosC * sinPhi1 + (term * cosPhi1) / p)));
    }


    @Override
    public final boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality but double precision does not \n" +
        //        "allow it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        //System.err.println("Fatal error : tried to test equality but double precision does not \n" +
        //        "allow it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "StereographicProjection : (" + centerOfProjection.az() + " ; " + centerOfProjection.alt() + ")";
    }
}
