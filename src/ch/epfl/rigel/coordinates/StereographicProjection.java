package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Functional class projecting 3D coords onto the 2D plane
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final HorizontalCoordinates centerOfProjection;
    private final double cosPhi1, sinPhi1;

    /**
     * Initialize projection for some given center of projection
     *
     * @param center (HorizontalCoordinates) center of projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.centerOfProjection = center;
        this.cosPhi1 = cos(center.alt());
        this.sinPhi1 = sin(center.alt());
    }

    /**
     * Horizontal -> Cartesian
     *
     * @param azAlt (HorizontalCoordinates) input horizontal coordinates
     * @return (CartesianCoordinates) Cartesian coordinate corresponding to azAlt
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        final double lambda = azAlt.az() - centerOfProjection.az();

        final double phi = azAlt.alt();
        final double sinP = 2 * sin(phi);

        final double term1 = cos(lambda - phi) + cos(lambda + phi);
        final double den = 1 / (sinPhi1 * sinP + cosPhi1 * term1 + 2); // +2 and remove 1/2

        return CartesianCoordinates.of(sin(lambda) * cos(phi) * 2 * den, (cosPhi1 * sinP - sinPhi1 * term1) * den);
    }

    /**
     * @param parallel (HorizontalCoordinates) the corresponding parallel on earth
     * @return (CartesianCoordinates) the center of the circle projected from parallel
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates parallel) {
        return CartesianCoordinates.of(0, cosPhi1 / (sin(parallel.alt()) + sinPhi1));
    }

    /**
     * @param parallel (HorizontalCoordinates) the corresponding parallel on earth
     * @return (double) the radius of the circle projected from parallel
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        return cos(parallel.alt()) / (sin(parallel.alt()) + sinPhi1);
    }

    /**
     * @param rad (double) size of the sphere
     * @return (double) diameter of the projected circle of angular size rad
     */
    public double applyToAngle(double rad) {
        return 2 * tan(rad / 4);
    }

    /**
     * Cartesian -> Horizontal
     *
     * @param cartesCoords (CartesianCoordinates) input cartesian coordinates
     * @return (HorizontalCoordinates) corresponding to cartesCoords
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates cartesCoords) {

        final double y = cartesCoords.y();
        final double x = cartesCoords.x();
        final double p2 = x * x + y * y;
        final double p = Math.sqrt(p2);

        final double den = 1 / (p2 + 1);
        final double sinC = 2 * p * den;
        final double cosC = (1 - p2) * den;
        final double term = y * sinC;

        return HorizontalCoordinates.of(
                Angle.normalizePositive((centerOfProjection.az() + atan2(x * sinC, p * cosPhi1 * cosC - term * sinPhi1))),
                asin(cosC * sinPhi1 + (term * cosPhi1) / p));
    }

    /**
     * @return (String) "StereographicProjection : (center.az() ; center.alt())"
     */
    @Override
    public String toString() {
        return "StereographicProjection : (" + centerOfProjection.az() + " ; " + centerOfProjection.alt() + ")";
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality but double precision does not \n" +
        //        "allow it.");
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
