package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.math.Angle.ofArcsec;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {
    private final static double EPSILON = 1e-4;

    @Test
    void apply() {
        ZonedDateTime when = ZonedDateTime.of(2009,7,6,0,0,0,0, ZoneOffset.UTC);

        double T    = J2000.julianCenturiesUntil(when);

        double epsilon = Polynomial.of(
                ofArcsec(0.00181), ofArcsec(-0.0006), ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45))
                .at(T);

        assertEquals( Angle.ofDeg(23.43805531),epsilon,EPSILON);

    }
}