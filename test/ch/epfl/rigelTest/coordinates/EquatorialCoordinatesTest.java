package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialCoordinatesTest {

    EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.of(1.5333, 45);



    @Test
    void ra() {
        assertEquals(0.40141700131243585, equatorialCoordinates.ra());

    }

    @Test
    void raDeg() {
        assertTrue(Math.abs(((23) - equatorialCoordinates.raDeg())) <= 0.001);

    }

    @Test
    void raHr() {
        assertTrue(Math.abs(Angle.toHr(Angle.ofDeg(23)) - equatorialCoordinates.raHr()) <= 0.001 );

    }

    @Test
    void dec() {
        assertEquals(Angle.ofDeg(45), equatorialCoordinates.dec());

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