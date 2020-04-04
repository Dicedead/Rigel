package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Equatorial coordinates system
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    private final static RightOpenInterval LON_INTERVAL_RAD_0toTAU = RightOpenInterval.of(0, 2 * Math.PI);
    private final static ClosedInterval LAT_INTERVAL_RAD_SYM_PI = ClosedInterval.symmetric(Math.PI);

    private final double raHr;

    /**
     * Constructor of EquatorialCoordinates
     *
     * @param ra  (double) input in radians for longitude
     * @param dec (double) input in radians for latitude
     */
    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
        this.raHr = Angle.toHr(ra);
    }

    /**
     * Constructs an EquatorialCoordinates object (factory constructor)
     *
     * @param ra  (double) input in radians for longitude
     * @param dec (double) input in radians for latitude
     */
    public static EquatorialCoordinates of(double ra, double dec) {

        return new EquatorialCoordinates(
                Preconditions.checkInInterval(LON_INTERVAL_RAD_0toTAU, ra),
                Preconditions.checkInInterval(LAT_INTERVAL_RAD_SYM_PI, dec));

    }

    /**
     * @return (double) right ascension in radians
     */
    public double ra() {
        return super.lon();
    }

    /**
     * @return (double) right ascension in degrees
     */
    public double raDeg() {
        return super.lonDeg();
    }

    /**
     * @return (double) right ascension in hours
     */
    public double raHr() {
        return raHr;
    }

    /**
     * @return (double) declination in rad
     */
    public double dec() {
        return super.lat();
    }

    /**
     * @return (double) declination in deg
     */
    public double decDeg() {
        return super.latDeg();
    }

    /**
     * toString override for EquatorialCoordinates
     *
     * @return (String) 4 decimal precision of right ascension in hours and declination in degrees
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "(ra=%.4fh, dec=%.4f°)",
                raHr,
                decDeg());
    }
}
