package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EclipticCoordinates extends SphericalCoordinates{

    private final static RightOpenInterval LON_INTERVAL_RAD_0toTAU = RightOpenInterval.of(0,Angle.TAU);
    private final static ClosedInterval LAT_INTERVAL_RAD_SYM_PI = ClosedInterval.symmetric(Math.PI);

    /**
     * Constructor of EclipticCoordinates
     * @param lon input in radians for longitude
     * @param lat input in radians for latitude
     */
    private EclipticCoordinates(double lon, double lat){
        super(lon,  lat);
    }


    /**
     * Constructs a EclipticCoordinates
     * @param lon input in rad for longitude
     * @param lat input in rad for latitude
     */
    public static EclipticCoordinates of(double lon, double lat)
    {

        return new EclipticCoordinates(
                (Preconditions.checkInInterval(LON_INTERVAL_RAD_0toTAU, lon)),
                (Preconditions.checkInInterval(LAT_INTERVAL_RAD_SYM_PI, lat)
                ));

    }
    /**
     * Getter for longitude in radians
     * @return longitude in radians
     */
    public double lon() { return super.lon(); }

    /**
     * Getter for longitude in degrees
     * @return longitude in degrees
     */
    public double lonDeg() {return super.lonDeg();}

    /**
     * Getter for latitude in radians
     * @return latitude in radians
     */
    public double lat(){ return super.lat(); }

    /**
     * Getter for latitude in degrees
     * @return latitude in degrees
     */
    public double latDeg(){return super.latDeg();}

    /**
     * toString override for EclipticCoordinates
     *
     * @return (String) 4 decimal precision of longitude and latitude in degrees
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "(λ=%.4f°, β=%.4f°)",
                lonDeg(),
                latDeg());
    }
}
