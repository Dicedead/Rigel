package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.Preconditions.epsilonIfZero;
import static ch.epfl.rigel.math.Angle.normalizePositive;
import static java.lang.Math.*;

/**
 * Functional conversion class from equatorial to horizontal coordinates
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double sinPhi;
    private final double cosPhi;
    private final double localTime;

    /**
     * Initialize conversion tool for a given place and time
     *
     * @param where (GeographicCoordinates) given place
     * @param when  (ZonedDateTime) given time
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.localTime = SiderealTime.local(when, where);
        this.sinPhi = sin(where.lat());
        this.cosPhi = epsilonIfZero(cos(where.lat()));
        //added for step 12 to correct latitude at the poles, was causing 1/0 divisions down the line in Stereographic
        //projection which created peculiar skies in 90 and -90 latitudes
    }

    /**
     * @param equCoords (EquatorialCoordinates) coordinates to convert
     * @return (HorizontalCoordinates) Horizontal coordinates corresponding to the input
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equCoords) {

        double dec = equCoords.dec();
        double sinDec = sin(dec);
        double hourAngle = localTime - equCoords.ra();

        double term1 = sinDec * sinPhi + cosPhi * cos(dec) * cos(hourAngle);
          /*Same playing around with trigonometry as for EclipticToEquatorialConversion in order to perform less
            computation.*/

        return HorizontalCoordinates.of(
                normalizePositive(atan2(-cosPhi * cos(dec) * sin(hourAngle), sinDec - sinPhi * term1)),
                asin(term1));
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
