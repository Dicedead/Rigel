package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;

import java.time.ZonedDateTime;
import java.util.function.Function;

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
        this.cosPhi = cos(where.lat());
    }

    /**
     * @param equCoords (EquatorialCoordinates) coordinates to convert
     * @return (HorizontalCoordinates) Horizontal coordinates corresponding to the input
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equCoords) {

        final double dec = equCoords.dec();
        final double sinDec = sin(dec);
        final double H = localTime - equCoords.ra();

        final double term1 = sinDec * sinPhi + cosPhi * cos(dec) * cos(H);
          /*Same playing around with trigonometry as for EclipticToEquatorialConversion in order to perform less
            computation.*/

        final double h = asin(term1);
        final double A = atan2(-cosPhi * cos(dec) * sin(H), sinDec - sinPhi * term1);

        return HorizontalCoordinates.of(normalizePositive(A), h);
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
