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

    private static final Polynomial POLYNOM = Polynomial.of(
            ofArcsec(0.00181), ofArcsec(-0.0006), ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45));

    /**
     * Initialize epsilon for calculations
     *
     * @param when (ZonedDateTime) time to convert
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {

        double julianCentsJ2000toWhen = J2000.julianCenturiesUntil(when);

        double epsilon = POLYNOM.at(julianCentsJ2000toWhen);

        this.sinEpsilon = sin(epsilon);
        this.cosEpsilon = cos(epsilon);
    }

    /**
     * @param eclipCoords (EclipticCoordinates) coordinates to convert
     * @return (EquatorialCoordinates) Equatorial coordinates corresponding to the input
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipCoords) {

        double lambda = eclipCoords.lon();
        double beta = eclipCoords.lat();

        double term1 = 2 * sin(beta);
        double term2 = sin(lambda - beta) + sin(lambda + beta);
            /*Playing around with some trigonometry, one can isolate these 2 common terms so there's no need to compute
              them twice.*/

        return EquatorialCoordinates.of(
                Angle.normalizePositive(atan2(term2 * cosEpsilon - term1 * sinEpsilon, 2 * cos(lambda) * cos(beta))),
                asin(0.5 * (term1 * cosEpsilon + term2 * sinEpsilon)));
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException("Fatal error : tried to test equality but double precision does not \n" +
                "allow it.");
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException("Fatal error : tried to hashcode but double precision does not \n" +
                "allow it.");
    }
}
