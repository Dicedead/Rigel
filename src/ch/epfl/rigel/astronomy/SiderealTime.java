package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofHr;

/**
 * Sidereal time simulation methods
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SiderealTime {

    private SiderealTime() {}

    private final static double S_ZERO_COEFF_0 = 0.000025862;
    private final static double[] S_ZERO_COEFFS = {2400.051336, 6.697374558};
    private final static double S_ONE_COEFF = 1.002737909;
    private final static Polynomial POLYNOM = Polynomial.of(S_ZERO_COEFF_0, S_ZERO_COEFFS);
    private final static double COEFF_TO_HOURS = 36e5;

    /**
     * Computes sidereal Greenwich time
     *
     * @param when (ZonedDateTime) instant
     * @return (double) Greenwich's sidereal time normalized to [0,TAU[ interval
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime dayOfWhen = when.truncatedTo(ChronoUnit.DAYS).withZoneSameInstant(ZoneOffset.UTC);
        double T = J2000.julianCenturiesUntil(dayOfWhen);
        double t = when.truncatedTo(ChronoUnit.DAYS).until(when,ChronoUnit.MILLIS) / COEFF_TO_HOURS;

        return normalizePositive(ofHr(POLYNOM.at(T) + S_ONE_COEFF * t));
    }

    /**
     * Computes local sidereal time normalized to [0,TAU[ interval
     *
     * @param when  (ZonedDateTime) time of point of interest
     * @param where (GeographicCoordinates) geographic coordinates of point of interest
     * @return (double) local sidereal time normalized to [0,TAU[ interval
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return normalizePositive(greenwich(when) + where.lon());
    }
}
