package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EquatorialCoordinates extends SphericalCoordinates{

    private final static RightOpenInterval LON_INTERVAL_RAD_0toTAU = RightOpenInterval.of(0, Angle.TAU);
    private final static ClosedInterval LAT_INTERVAL_RAD_SYM_PI = ClosedInterval.symmetric(Math.PI);

    /**
     * Constructor of EquatorialCoordinates
     * @param ra input in radians for longitude
     * @param dec input in radians for latitude
     */
    private EquatorialCoordinates(double ra, double dec){
        super(ra, dec);
    }

    /**
     * Constructs a EquatorialCoordinates
     * @param ra input in radians for longitude
     * @param dec input in radians for latitude
     */
    public static EquatorialCoordinates of(double ra, double dec)
    {

        return new EquatorialCoordinates(
                Preconditions.checkInInterval(LON_INTERVAL_RAD_0toTAU, ra),
                Preconditions.checkInInterval(LAT_INTERVAL_RAD_SYM_PI, dec));

    }

    /**
     * Getter for right ascension in radians
     * @return right ascension in radians
     */
    public double ra() { return super.lon(); }

    /**
     * Getter for right ascension in degrees
     * @return right ascension in degrees
     */
    public double raDeg() {return Angle.toDeg(super.lon());}

    /**
     * Getter for right ascension in hours
     * @return latitude in radians
     */
    public double raHr() { return Angle.toHr(super.lon()); }

    /**
     * Getter for declination in rad
     * @return declination in rad
     */
    public double dec() {return super.lat();}

    /**
     * Getter for declination in deg
     * @return declination in deg
     */
    public double decDeg() {return super.latDeg();}

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "(ra=%.4fh, dec=%.4f°)",
                raHr(),
                decDeg());
    }
}
