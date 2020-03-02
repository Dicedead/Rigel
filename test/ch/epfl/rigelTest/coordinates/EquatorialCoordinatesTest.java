package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import static ch.epfl.rigel.math.Angle.ofDeg;
import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.*;

class EquatorialCoordinatesTest {

    private static final double EPSILON = 1e-150;
    EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(ofDeg(23), ofDeg(45));

    @Test
    void ra() {
        assertEquals(ofDeg(23), equatorialCoordinates.ra(), EPSILON);

    }


    @Test
    void raDeg() {
        assertEquals(23, equatorialCoordinates.raDeg(), EPSILON);

    }

    @Test
    void raHr() {
        assertEquals(Angle.toHr(ofDeg(23)), equatorialCoordinates.raHr(), EPSILON);

    }

    @Test
    void dec() {
        assertEquals(ofDeg(45), equatorialCoordinates.dec());

    }

    @Test
    void decDeg() {
        assertEquals((45), equatorialCoordinates.decDeg());

    }

    @Test
    void testToString() {
            assertEquals("(ra=1.5333h, dec=45.0000°)", equatorialCoordinates.toString());
    }

    @Test
    void equOfWorksWithValidCoordinates() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var ra = rng.nextDouble(0, 2d * PI);
            var dec = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = EquatorialCoordinates.of(ra, dec);
            assertEquals(ra, c.ra(), 1e-8);
            assertEquals(dec, c.dec(), 1e-8);
        }
    }

    @Test
    void equOfFailsWithInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates.of(2d * PI + 1e-8, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates.of(-1e-8, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates.of(0, PI + 1e-8);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates.of(0, -(PI + 1e-8));
        });
    }

    @Test
    void raDegAndDecDegReturnCoordinatesInDegrees() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var ra = rng.nextDouble(0, 2d * PI);
            var dec = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = EquatorialCoordinates.of(ra, dec);
            assertEquals(Math.toDegrees(ra), c.raDeg(), 1e-8);
            assertEquals(Math.toDegrees(dec), c.decDeg(), 1e-8);
        }
    }

    @Test
    void raHrReturnsRightAscensionInHours() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var ra = rng.nextDouble(0, 2d * PI);
            var dec = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = EquatorialCoordinates.of(ra, dec);
            assertEquals(Math.toDegrees(ra) / 15d, c.raHr(), 1e-8);
        }
    }

    @Test
    void equEqualsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            var c = EquatorialCoordinates.of(0, 0);
            c.equals(c);
        });
    }

    @Test
    void equHashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            EquatorialCoordinates.of(0, 0).hashCode();
        });
    }
}