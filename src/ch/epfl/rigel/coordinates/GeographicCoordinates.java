package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Geographic coordinates system
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    private final static RightOpenInterval LON_INTERVAL_DEG_SYM_360 = RightOpenInterval.symmetric(360);
    private final static ClosedInterval LAT_INTERVAL_DEG_SYM_180 = ClosedInterval.symmetric(180);

    /**
     * Constructor of GeographicCoordinates
     *
     * @param longitude (double) input in radians for longitude
     * @param latitude  (double) input in radians for latitude
     */
    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Constructs a GeographicCoordinates (factory constructor)
     *
     * @param lonDeg (double) input in degrees for longitude
     * @param latDeg (double) input in degrees for latitude
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {

        return new GeographicCoordinates(
                Angle.ofDeg(Preconditions.checkInInterval(LON_INTERVAL_DEG_SYM_360, lonDeg)),
                Angle.ofDeg(Preconditions.checkInInterval(LAT_INTERVAL_DEG_SYM_180, latDeg)));

    }

    /**
     * Checks that the passed angle is a valid longitude in degrees
     *
     * @param lonDeg (double) angle input in degrees
     * @return (boolean) true iff it is between -180° and 180°
     */
    static public boolean isValidLonDeg(double lonDeg) {
        return LON_INTERVAL_DEG_SYM_360.contains(lonDeg);
    }

    /**
     * Checks that the passed angle is a valid latitude in degrees
     *
     * @param latDeg (double) angle input in degrees
     * @return (boolean) true iff it is between -90° and 90°
     */
    static public boolean isValidLatDeg(double latDeg) {
        return LAT_INTERVAL_DEG_SYM_180.contains(latDeg);
    }


    /**
     * @return (double) longitude in radians
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * @return (double) longitude in degrees
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * @return (double) latitude in radians
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * @return (double) latitude in degrees
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }

    /**
     * toString override for GeographicCoordinates
     *
     * @return (String) 4 decimal precision of longitude and latitude in degrees
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "(lon=%.4f°, lat=%.4f°)",
                lonDeg(),
                latDeg());
    }
}
