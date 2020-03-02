package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static ch.epfl.rigel.math.Angle.ofDeg;
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
}