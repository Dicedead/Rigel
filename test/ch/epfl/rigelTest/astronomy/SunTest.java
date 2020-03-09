package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SunTest {

    private static final Sun sunTest = new Sun(EclipticCoordinates.of(Angle.ofDeg(5.64),Angle.ofDeg(43)),
            EquatorialCoordinates.of(Angle.ofDeg(5),Angle.ofDeg(6)), 5.15903f,7.1589f);
    private final static double EPSILON = 1e-6;

    @Test
    void throwsOnNullEclip() {
        EclipticCoordinates eclip = null;
        assertThrows(NullPointerException.class, () -> {
            new Sun(eclip, EquatorialCoordinates.of(Angle.ofDeg(5),Angle.ofDeg(6)),
                    5,7);
        });
    }

    @Test
    void name() {
        assertEquals("Soleil",sunTest.name());
    }

    @Test
    void magnitude() {
        assertEquals(-26.7,sunTest.magnitude(), EPSILON);
    }

    @Test
    void eclipticPos() {
        assertEquals(EclipticCoordinates.of(Angle.ofDeg(5.64),Angle.ofDeg(43)).lon(),sunTest.eclipticPos().lon());
        assertEquals(EclipticCoordinates.of(Angle.ofDeg(5.64),Angle.ofDeg(43)).lat(),sunTest.eclipticPos().lat());
    }

    @Test
    void meanAnomaly() {
        assertEquals(7.1589,sunTest.meanAnomaly(),EPSILON);
    }
}