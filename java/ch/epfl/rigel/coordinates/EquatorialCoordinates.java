package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EquatorialCoordinates extends SphericalCoordinates{
    /**
     * Constructor of GeographicCoordinates
     * @param ra input in radians for longitude
     * @param deg input in radians for latitude
     */
    private EquatorialCoordinates(double ra, double deg){
        super(ra,  deg);
    }

    /**
     * Constructs a GeographicCoordinates
     * @param ra input in degrees for longitude
     * @param deg input in degrees for latitude
     */
    public static EquatorialCoordinates ofDeg(double ra, double deg)
    {

        return new EquatorialCoordinates(
                Angle.ofHr(Preconditions.checkInInterval(RightOpenInterval.of(0, 24), ra)),
                Angle.ofDeg(Preconditions.checkInInterval(RightOpenInterval.symmetric(180), deg)
                ));

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
                "(ra=%.4fh, dec=%4f°)",
                lonDeg(),
                latDeg());
    }
}
