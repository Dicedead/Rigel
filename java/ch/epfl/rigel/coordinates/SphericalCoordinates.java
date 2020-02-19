package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.math.Angle.toDeg;

abstract class SphericalCoordinates
{

    final private double longitude;
    final private double latitude;

    /**
     * Constructor for SphericalCoordinates
     * @param longitude input in radians for longitude
     * @param latitude input in radians for latitude
     */
    SphericalCoordinates(double longitude, double latitude)
    {
        this.latitude   = latitude;
        this.longitude  = longitude;
    }

    /**
     * Getter for longitude in radians
     * @return longitude in radians
     */
    double lon() { return longitude; }

    /**
     * Getter for longitude in degrees
     * @return longitude in degrees
     */
    double lonDeg() {return toDeg(longitude);}

    /**
     * Getter for latitude in radians
     * @return latitude in radians
     */
    double lat(){ return latitude; }

    /**
     * Getter for latitude in degrees
     * @return latitude in degrees
     */
    double latDeg(){return toDeg(latitude);}

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(Object o) {
        System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
                "allows it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
                "allows it.");
        throw new UnsupportedOperationException();
    }
}
