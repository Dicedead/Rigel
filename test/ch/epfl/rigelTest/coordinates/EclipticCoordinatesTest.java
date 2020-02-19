package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EclipticCoordinatesTest {

    EclipticCoordinates eclipticCoordinates = EclipticCoordinates.ofDeg(23, 45);


    @Test
    void ofDeg() {

        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.ofDeg(23, 180);
        EclipticCoordinates eclipticCoordinates1 = EclipticCoordinates.ofDeg(0, 100);
        EclipticCoordinates eclipticCoordinates2 = EclipticCoordinates.ofDeg(-300, 2);

    }
    @Test
    void lon() { assertEquals(Angle.ofDeg(23) , eclipticCoordinates.lon()); }

    @Test
    void lonDeg() { assertEquals((23) , eclipticCoordinates.lon()); }

    @Test
    void lat() { assertEquals(Angle.ofDeg(45) , eclipticCoordinates.lat()); }

    @Test
    void latDeg() { assertEquals(45 , eclipticCoordinates.latDeg()); }

    @Test
    void testToString() {
        assertEquals("(λ=23.000°, β=45.0000°)", eclipticCoordinates.toString());
    }
}