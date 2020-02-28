package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicCoordinatesTest {
    GeographicCoordinates geographicCoordinates = GeographicCoordinates.ofDeg(23, 45);

    @Test
    void ofDeg() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates geographicCoordinates = GeographicCoordinates.ofDeg(23, 180);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates geographicCoordinates1 = GeographicCoordinates.ofDeg(0, 100);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates geographicCoordinates2 = GeographicCoordinates.ofDeg(-300, 2);
        });

    }

    @Test
    void isValidLonDeg() {

        assertTrue(GeographicCoordinates.isValidLonDeg(23));
        assertTrue(GeographicCoordinates.isValidLonDeg(0));
        assertTrue(GeographicCoordinates.isValidLonDeg(-180));
        assertFalse(GeographicCoordinates.isValidLonDeg(180));
        assertFalse(GeographicCoordinates.isValidLonDeg(-400));


    }

    @Test
    void isValidLatDeg() {

        assertTrue(GeographicCoordinates.isValidLatDeg(23));
        assertTrue(GeographicCoordinates.isValidLatDeg(0));
        assertFalse(GeographicCoordinates.isValidLatDeg(-180));
        assertFalse(GeographicCoordinates.isValidLatDeg(180));
        assertFalse(GeographicCoordinates.isValidLatDeg(-400));
    }

    @Test
    void lon() {
        assertEquals(Angle.ofDeg(23), geographicCoordinates.lon());
    }

    @Test
    void lonDeg() {
        assertEquals(23, geographicCoordinates.lonDeg());
    }

    @Test
    void lat() {
        assertEquals(Angle.ofDeg(45), geographicCoordinates.lat());
    }

    @Test
    void latDeg() {
        assertEquals(45, geographicCoordinates.latDeg());
    }


    @Test
    void testToString() {
        assertEquals("(lon=23.0000°, lat=45.0000°)", geographicCoordinates.toString());

    }
}