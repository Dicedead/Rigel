package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
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

    private final double epsilon;
    private final double sinEpsilon;
    private final double cosEpsilon;
    private final double phi, sinPhi, cosPhi;
    private final GeographicCoordinates geographicCoordinates;
    private final ZonedDateTime now;

    /**
     * Initialize epsilon for calculations
     * @param when time to convert
     */
    public EquatorialToHorizontalConversion(ZonedDateTime  when, GeographicCoordinates where)
    {
        double T    = J2000.julianCenturiesUntil(when);
        epsilon     = Polynomial.of(
                ofArcsec(0.00181), ofArcsec(-0.0006), ofArcsec(-46.815), Angle.ofDMS(23, 26,21.45))
                .at(T);
        sinEpsilon  = sin(epsilon);
        cosEpsilon  = cos(epsilon);
        phi         = where.lat();
        sinPhi      = sin(phi);
        cosPhi      = cos(phi);
        now = when;
        geographicCoordinates = where;

    }

    /**
     *
     * @param equatorialCoordinates coordinates to convert
     * @return Horizontal coordinates corresponding to the input
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {

        double lambda   = equatorialCoordinates.lon();

        double beta     = equatorialCoordinates.lat();

        double term1    = 2*sinEpsilon*sin(beta);
        double term2    = cosEpsilon*(sin(lambda - beta) + sin(lambda + beta));
        double term3    = 0.5* term1 + term2;
        double ra       = atan2(term2 - term1, 2*cos(lambda)*cos(beta));

        double A        = acos((term3 - sinPhi*sin(ra))/cos(ra));
        double h        = asin(term3*sinPhi + cosPhi*Math.sqrt(1 - term3*term3)*cos(SiderealTime.local(now, geographicCoordinates) - ra));


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
