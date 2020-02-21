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
    void alt() {
        assertEquals(Angle.ofDeg(45), horizontalCoordinates.alt());

    }

    @Test
    void altDeg() {
        assertEquals(45, horizontalCoordinates.altDeg());

    }

    @Test
    void angularDistanceTo() {

        HorizontalCoordinates rolexCoords = HorizontalCoordinates.ofDeg(6.5682, 46.5183);
        HorizontalCoordinates zurichCoords = HorizontalCoordinates.ofDeg(8.5476, 47.3763);
        HorizontalCoordinates eiffelTowerCoords = HorizontalCoordinates.ofDeg(2.294694, 48.858093);

        assertEquals(0.0279, rolexCoords.angularDistanceTo(zurichCoords), EPSILON);
        assertEquals(0, eiffelTowerCoords.angularDistanceTo(eiffelTowerCoords));
        assertEquals(0.06470217819714881, eiffelTowerCoords.angularDistanceTo(rolexCoords), EPSILON);
    }

    @Test
    void azOctantName() {
        assertEquals("NO", HorizontalCoordinates.ofDeg(335, 0)
                .azOctantName("N", "E", "S", "O"));
        assertEquals("N", HorizontalCoordinates.ofDeg(337.5, 0)
                .azOctantName("N", "E", "S", "O"));
        assertEquals("NE", HorizontalCoordinates.ofDeg(27.5, 0)
                .azOctantName("N", "E", "S", "O"));
        assertEquals("SO", HorizontalCoordinates.ofDeg(202.5, 0)
                .azOctantName("N", "E", "S", "O"));
    }

    @Test
    void testToString() {
        assertEquals("(az=23.0000°, alt=45.0000°)", horizontalCoordinates.toString());
    }
}