package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.math.Angle.ofArcsec;
import static java.lang.Math.*;

/**
 * Functional conversion class from ecliptic to equatorial coordinates
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double sinEpsilon;
    private final double cosEpsilon;

    private final static Polynomial POLYNOM = Polynomial.of(
            ofArcsec(0.00181), ofArcsec(-0.0006), ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45));

    /**
     * Initialize epsilon for calculations
     *
     * @param when (ZonedDateTime) time to convert
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        final double T = J2000.julianCenturiesUntil(when);

        final double epsilon = POLYNOM.at(T);

        this.sinEpsilon = sin(epsilon);
        this.cosEpsilon = cos(epsilon);
    }

    /**
     * @param eclipCoords (EclipticCoordinates) coordinates to convert
     * @return (EquatorialCoordinates) Equatorial coordinates corresponding to the input
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipCoords) {

        final double lambda = eclipCoords.lon();
        final double beta = eclipCoords.lat();

        final double term1 = 2 * sin(beta);
        final double term2 = sin(lambda - beta) + sin(lambda + beta);
            /*Playing around with some trigonometry, one can isolate these 2 common terms so there's no need to compute
              them twice.*/

        final double ra = atan2(term2 * cosEpsilon - term1 * sinEpsilon, 2 * cos(lambda) * cos(beta));
        final double dec = asin(0.5 * (term1 * cosEpsilon + term2 * sinEpsilon));

        return EquatorialCoordinates.of(Angle.normalizePositive(ra), dec);
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality but double precision does not \n" +
        //        "allow it.");
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
