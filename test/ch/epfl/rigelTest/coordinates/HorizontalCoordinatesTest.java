package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalCoordinatesTest {

    private static final double EPSILON = 1e-4;

    HorizontalCoordinates horizontalCoordinates = HorizontalCoordinates.ofDeg(23, 45);


    @Test
    void az() {
        assertEquals(Angle.ofDeg(23), horizontalCoordinates.az());
    }

    @Test
    void azDeg() {
        assertEquals((23), horizontalCoordinates.azDeg());

    }

    @Test
    void azOctantName() {
    }

    @Test
    void alt() {
        assertEquals(Angle.ofDeg(45), horizontalCoordinates.alt());

    }

    @Test
    void altDeg() {
        assertEquals(45, horizontalCoordinates.altDeg());

    }

    @Test
    void angularDistanceTo() {
        assertEquals(0.02793,
                HorizontalCoordinates.ofDeg(6.5682, 46.5183)
                        .angularDistanceTo(HorizontalCoordinates.ofDeg(8.5476, 47.3763)),EPSILON);
    }

    @Test
    void testToString() {
        assertEquals("(az=23.0000°, alt=45.0000°)", horizontalCoordinates.toString());
    }
}