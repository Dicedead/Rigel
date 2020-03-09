package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.math.Angle.normalizePositive;
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
    private final double localTime;

    /**
     * Initialize epsilon for calculations
     * @param when time to convert
     */
    public EquatorialToHorizontalConversion(ZonedDateTime  when, GeographicCoordinates where)
    {
        localTime = SiderealTime.local(when, where);

        sinPhi      = sin(where.lat());
        cosPhi      = cos(where.lat());
    }

    /**
     * @param equatorialCoordinates coordinates to convert
     * @return Horizontal coordinates corresponding to the input
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {

        double ra       = equatorialCoordinates.ra();
        double dec      = equatorialCoordinates.dec();
        double sinDec   = sin(dec);
        double H        = localTime - ra;

        double term1    = sinDec*sinPhi + cosPhi*cos(dec)*cos(H);
          /*Same playing around with trigonometry as for EclipticToEquatorialConversion in order to perform less
            computation.*/

        double h        = asin(term1);
        double A        = atan2(-cosPhi*cos(dec)*sin(H),sinDec - sinPhi * term1);

        return HorizontalCoordinates.of(normalizePositive(A), h);
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
