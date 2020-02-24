package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;

import java.time.ZonedDateTime;
import java.util.function.Function;

public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates>
{

    double epsilon;
    double sinEpsilon;
    double cosEpsilon;
    public EclipticToEquatorialConversion (ZonedDateTime  when)
    {
    }
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipticCoordinates) {
        double dec = atan2();
        double ra;
        return null;
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
