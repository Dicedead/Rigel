package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.math.Angle.ofArcsec;
import static org.junit.jupiter.api.Assertions.*;

class MyEclipticToEquatorialConversionTest {
    private final static double EPSILON = 1e-6;
    private final static double EPSILON2 = 1e-8;


    @Test
    void epsilon() {
        ZonedDateTime when = ZonedDateTime.of(2009,7,6,0,0,0,0, ZoneOffset.UTC);

        double T    = J2000.julianCenturiesUntil(when);

        double epsilon = Polynomial.of(
                ofArcsec(0.00181), ofArcsec(-0.0006), ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45))
                .at(T);

        assertEquals( Angle.ofDeg(23.43805531),epsilon,EPSILON2);
    }

    @Test
    void apply() {
        ZonedDateTime when = ZonedDateTime.of(2009,7,6,0,0,0,0,ZoneOffset.UTC);
        EclipticToEquatorialConversion converter = new EclipticToEquatorialConversion(when);
        EclipticCoordinates eclipCoords = EclipticCoordinates.of(Angle.ofDMS(139,41,10),Angle.ofDMS(4,52,31));
        EquatorialCoordinates equatCoords = converter.apply(eclipCoords);

        assertEquals(9+34/60.0+53.32/3600,equatCoords.raHr(),EPSILON);
        assertEquals(Angle.ofDMS(19,32,6.01),equatCoords.dec(),EPSILON2);
    }
}