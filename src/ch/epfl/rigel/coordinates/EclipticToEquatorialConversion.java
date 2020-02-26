package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofArcsec;
import static java.lang.Math.*;

/**
 * Conversion class from ecliptic to equatorial coordinates
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates>
{

    private final double sinEpsilon;
    private final double cosEpsilon;

    /**
     * Initialize epsilon for calculations
     * @param when time to convert
     */
    public EclipticToEquatorialConversion (ZonedDateTime  when)
    {
        double T    = J2000.julianCenturiesUntil(when);

        double epsilon = Polynomial.of(
                ofArcsec(0.00181), ofArcsec(-0.0006), ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45))
                .at(T);

        sinEpsilon  = sin(epsilon);
        cosEpsilon  = cos(epsilon);
    }

    /**
     *
     * @param eclipticCoordinates coordinates to convert
     * @return Equatorial coordinates corresponding to the input
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipticCoordinates) {

        double lambda   = eclipticCoordinates.lon();
        double beta     = eclipticCoordinates.lat();

        double term1    = 2*sin(beta);
        double term2    = sin(lambda - beta) + sin(lambda + beta);

        double ra       = atan2(term2*cosEpsilon - term1*sinEpsilon, 2*cos(lambda)*cos(beta));

        double dec      = asin(0.5*(term1*cosEpsilon + term2*sinEpsilon));//asin(sin(beta)*cosEpsilon + cos(beta)*sinEpsilon*sin(lambda));//

        return EquatorialCoordinates.of(ra, dec);
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
