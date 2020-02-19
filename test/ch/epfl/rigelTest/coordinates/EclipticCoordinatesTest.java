package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EclipticCoordinatesTest {

    EclipticCoordinates eclipticCoordinates;
    @BeforeAll
    void setUp ()
    {
        eclipticCoordinates = EclipticCoordinates.ofDeg(23, 45);
    }
    @Test
    void ofDeg() {
        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.ofDeg(23, 180);
        EclipticCoordinates eclipticCoordinates1 = EclipticCoordinates.ofDeg(0, 100);
        EclipticCoordinates eclipticCoordinates2 = EclipticCoordinates.ofDeg(-300, 2);


    }
    @Test
    void lon() {
    }

    @Test
    void lonDeg() {
    }

    @Test
    void lat() {
    }

    @Test
    void latDeg() {
    }

    @Test
    void testToString() {
    }
}