package ch.epfl.rigel.coordinates;
import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;


public final class GeographicCoordinates extends SphericalCoordinates
{
    /**
     * Constructor of GeographicCoordinates
     * @param longitude input in radians for longitude
     * @param latitude input in radians for latitude
     */
    private GeographicCoordinates(double longitude, double latitude){
        super(longitude,  latitude);
    }

    /**
     * Constructs a GeographicCoordinates
     * @param lonDeg input in degrees for longitude
     * @param latDeg input in degrees for latitude
     */
    GeographicCoordinates ofDeg(double lonDeg, double latDeg)
    {
        Preconditions.checkInInterval(RightOpenInterval.symmetric(360), lonDeg);
        Preconditions.checkInInterval(RightOpenInterval.symmetric(360), latDeg);

        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));

    }

    /**
     * Checks that the passed angle is a valid longitude in degrees
     * @param lonDeg
     * @return
     */
    static  public  boolean isValidLonDeg(double lonDeg)
    {

    }
    boolean isValidLatDeg(double latDeg)
    {

    }
}
