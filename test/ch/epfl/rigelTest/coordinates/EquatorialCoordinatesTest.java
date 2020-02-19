package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialCoordinatesTest {

    EquatorialCoordinates equatorialCoordinates = EquatorialCoordinates.ofDeg(23, 45);



    @Test
    void ra() {
        assertEquals(Angle.ofDeg(23), equatorialCoordinates.ra());

    }

    @Test
    void raDeg() {
        assertEquals((23), equatorialCoordinates.raDeg());

    }

    @Test
    void raHr() {
        assertEquals(Angle.toHr(Angle.ofDeg(23)), equatorialCoordinates.raHr());

    }

    @Test
    void dec() {
        assertEquals(Angle.ofDeg(23), equatorialCoordinates.dec());

    }

    @Test
    void decDeg() {
        assertEquals((45), equatorialCoordinates.decDeg());

    }

    @Test
    void testToString() {
            assertEquals("(λ=23.0000°, β=45.0000°)", equatorialCoordinates.toString());
    }
}