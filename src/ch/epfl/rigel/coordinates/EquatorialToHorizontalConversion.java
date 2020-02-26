package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofArcsec;
import static java.lang.Math.*;

/**
 * Conversion class from equatorial to horizontal coordinates
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates>
{

    private final double sinPhi;
    private final double cosPhi;
    private final GeographicCoordinates geographicCoordinates;
    private final ZonedDateTime now;

    /**
     * Initialize epsilon for calculations
     * @param when time to convert
     */
    public EquatorialToHorizontalConversion(ZonedDateTime  when, GeographicCoordinates where)
    {
        geographicCoordinates = where;


        sinPhi      = sin(geographicCoordinates.lat());
        cosPhi      = cos(geographicCoordinates.lat());
        now         = when;

    }

    /**
     * An algebraic modification has been applied  to the function to maje iit more efficient
     * @param equatorialCoordinates coordinates to convert
     * @return Horizontal coordinates corresponding to the input
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {

        double ra       = equatorialCoordinates.ra();
        double dec      = equatorialCoordinates.dec();
        double sinDec   = sin(dec);

        double term1    = sinDec*sinPhi + cosPhi*cos(dec)*cos(SiderealTime.local(now, geographicCoordinates) - ra);
        double h        = asin(term1);
        double A        = acos((sinDec - sinPhi*term1)/(cos(h)*cosPhi));

        return HorizontalCoordinates.of(A, h);
    }

    @Override
    public final boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
        //        "allows it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        //System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
        //        "allows it.");
        throw new UnsupportedOperationException();
    }
}
