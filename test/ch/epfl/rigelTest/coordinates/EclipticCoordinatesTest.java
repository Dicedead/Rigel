package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.*;

class EclipticCoordinatesTest {

    EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(Angle.ofDeg(23), Angle.ofDeg(45));
    EclipticCoordinates eclipticCoordinates2 = EclipticCoordinates.of(Angle.ofDeg(350), Angle.ofDeg(80));

    @Test
    void of() {
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates eclipticCoordinates1 =
                    EclipticCoordinates.of(Angle.ofDeg(23), Angle.ofDeg(180));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates eclipticCoordinates2 =
                    EclipticCoordinates.of(Angle.ofDeg(-300), Angle.ofDeg(2));
        });

    }

    @Test
    void lon() {
        assertEquals(Angle.ofDeg(23), eclipticCoordinates.lon());
        assertEquals(Angle.ofDeg(350), eclipticCoordinates2.lon());
    }

    @Test
    void lonDeg() {
        assertEquals(23, eclipticCoordinates.lonDeg());
    }

    @Test
    void lat() {
        assertEquals(Angle.ofDeg(45), eclipticCoordinates.lat());
        assertEquals(Angle.ofDeg(80), eclipticCoordinates2.lat());
    }

    @Test
    void latDeg() {
        assertEquals(45, eclipticCoordinates.latDeg());
    }

    @Test
    void testToString() {
        assertEquals("(λ=23.0000°, β=45.0000°)", eclipticCoordinates.toString());
    }

    @Test
    void eclOfWorksWithValidCoordinates() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var lon = rng.nextDouble(0, 2d * PI);
            var lat = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = EclipticCoordinates.of(lon, lat);
            assertEquals(lon, c.lon(), 1e-8);
            assertEquals(lat, c.lat(), 1e-8);
        }
    }

    @Test
    void eclOfFailsWithInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(2d * PI + 1e-8, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(-1e-8, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(0, PI + 1e-8);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(0, -(PI + 1e-8));
        });
    }

    @Test
    void lonDegAndLatDegReturnCoordinatesInDegrees() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var lon = rng.nextDouble(0, 2d * PI);
            var lat = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = EclipticCoordinates.of(lon, lat);
            assertEquals(Math.toDegrees(lon), c.lonDeg(), 1e-8);
            assertEquals(Math.toDegrees(lat), c.latDeg(), 1e-8);
        }
    }

    @Test
    void ecEqualsThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            var c = EclipticCoordinates.of(0, 0);
            c.equals(c);
        });
    }

    @Test
    void ecHashCodeThrowsUOE() {
        assertThrows(UnsupportedOperationException.class, () -> {
            EclipticCoordinates.of(0, 0).hashCode();
        });
    }
}
