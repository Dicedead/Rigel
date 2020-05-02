package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.math.Angle.toDeg;

/**
 * General spherical coordinates system
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
abstract class SphericalCoordinates {

    final private double longitude;
    final private double latitude;
    final private double lonDeg;
    final private double latDeg;

    /**
     * Constructor for SphericalCoordinates
     *
     * @param longitude (double) input in radians for longitude
     * @param latitude  (double) input in radians for latitude
     */
    SphericalCoordinates(double longitude, double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lonDeg = toDeg(longitude);
        this.latDeg = toDeg(latitude);
    }

    /**
     * @return (double) longitude in radians
     */
    double lon() {
        return longitude;
    }

    /**
     * @return (double) longitude in degrees
     */
    double lonDeg() {
        return lonDeg;
    }

    /**
     * @return (double) latitude in radians
     */
    double lat() {
        return latitude;
    }

    /**
     * @return (double) latitude in degrees
     */
    double latDeg() {
        return latDeg;
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Fatal error : tried to test equality but double precision does not \n" +
                "allow it.");
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException("Fatal error : tried to hashcode but double precision does not \n" +
                "allow it.");
    }
}
